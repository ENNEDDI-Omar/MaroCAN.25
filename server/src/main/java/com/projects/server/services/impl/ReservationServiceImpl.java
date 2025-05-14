package com.projects.server.services.impl;

import com.projects.server.domain.entities.Match;
import com.projects.server.domain.entities.Ticket;
import com.projects.server.domain.entities.TicketOrder;
import com.projects.server.domain.entities.User;
import com.projects.server.domain.enums.PaymentStatusType;
import com.projects.server.domain.enums.SectionType;
import com.projects.server.domain.enums.TicketStatusType;
import com.projects.server.dto.request.TicketPurchaseRequest;
import com.projects.server.dto.request.TicketReservationRequest;
import com.projects.server.dto.response.OrderResponse;
import com.projects.server.dto.response.PaymentInitResponse;
import com.projects.server.dto.response.TicketResponse;
import com.projects.server.exceptions.ResourceNotFoundException;
import com.projects.server.exceptions.AuthenticationException;
import com.projects.server.exceptions.PaymentException;
import com.projects.server.mapper.TicketMapper;
import com.projects.server.repositories.MatchRepository;
import com.projects.server.repositories.TicketOrderRepository;
import com.projects.server.repositories.TicketRepository;
import com.projects.server.repositories.UserRepository;
import com.projects.server.services.TicketDeliveryService;
import com.projects.server.services.interfaces.PaymentService;
import com.projects.server.services.interfaces.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final TicketRepository ticketRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final TicketOrderRepository ticketOrderRepository;
    private final TicketMapper ticketMapper;
    private final PaymentService paymentService;
    private final TicketDeliveryService ticketDeliveryService;


    private static final int RESERVATION_DURATION_MINUTES = 30;

    @Override
    @Transactional
    public List<TicketResponse> reserveTickets(TicketReservationRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match non trouvé avec l'id: " + request.getMatchId()));

        // Vérifier que l'utilisateur n'a pas déjà réservé ou acheté plus que le maximum autorisé
        int alreadyReservedOrPurchased = ticketRepository.countByMatchAndUserEmail(match.getId(), userEmail);

        if (alreadyReservedOrPurchased + request.getQuantity() > 5) {
            throw new IllegalArgumentException("Vous ne pouvez pas réserver plus de 5 billets par match. Vous avez déjà " +
                    alreadyReservedOrPurchased + " billets pour ce match.");
        }

        // Vérifier si suffisamment de billets sont disponibles
        Map<SectionType, Integer> availableTicketsMap = match.getAvailableTickets();
        Integer currentAvailable = availableTicketsMap.getOrDefault(request.getSectionType(), 0);

        if (currentAvailable < request.getQuantity()) {
            throw new IllegalStateException("Nombre insuffisant de billets disponibles pour cette section");
        }

        // Créer les tickets (approche sans générer tous les tickets à l'avance)
        List<Ticket> reservedTickets = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(RESERVATION_DURATION_MINUTES);
        double ticketPrice = match.getPriceForSection(request.getSectionType());

        for (int i = 0; i < request.getQuantity(); i++) {
            Ticket ticket = Ticket.builder()
                    .match(match)
                    .sectionType(request.getSectionType())
                    .price(ticketPrice)
                    .status(TicketStatusType.RESERVED)
                    .reservationTime(now)
                    .expirationTime(expirationTime)
                    .ticketCode(generateTicketCode(match, request.getSectionType()))
                    .build();

            reservedTickets.add(ticket);
        }

        // Sauvegarder les tickets
        reservedTickets = ticketRepository.saveAll(reservedTickets);

        // Mettre à jour le compteur de billets disponibles
        availableTicketsMap.put(request.getSectionType(), currentAvailable - request.getQuantity());
        matchRepository.save(match);

        return reservedTickets.stream()
                .map(ticketMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentInitResponse initiatePayment(TicketPurchaseRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);

        // Récupérer les billets réservés
        List<Ticket> tickets = ticketRepository.findAllById(request.getTicketIds());

        // Vérifications
        validateTicketsForPurchase(tickets);

        // Créer une commande
        TicketOrder order = TicketOrder.builder()
                .user(user)
                .orderReference("ORD-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10).toUpperCase())
                .paymentStatus(PaymentStatusType.PENDING)
                .totalAmount(0.0)
                .build();

        // Associer les billets à la commande et calculer le montant total
        for (Ticket ticket : tickets) {
            order.addTicket(ticket);
        }

        order.calculateTotalAmount();

        // Sauvegarder la commande
        order = ticketOrderRepository.save(order);

        log.info("Commande créée: {} pour l'utilisateur: {} avec {} billets pour un montant de {} MAD",
                order.getOrderReference(), userEmail, tickets.size(), order.getTotalAmount());

        try {
            // Initialiser le paiement avec Stripe
            return paymentService.initiatePayment(order);
        } catch (Exception e) {
            // En cas d'erreur, annuler la commande et libérer les billets
            rollbackOrder(order);
            throw new PaymentException("Erreur lors de l'initialisation du paiement: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public OrderResponse completePayment(String sessionId, String userIdentifier) {
        log.info("Tentative de compléter le paiement pour session: {}, userIdentifier: {}", sessionId, userIdentifier);

        // Récupérer la commande associée à cette session
        TicketOrder order = ticketOrderRepository.findByPaymentReference(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée pour la session: " + sessionId));

        // Récupérer l'utilisateur à partir de l'identifiant (email ou username)
        User authenticatedUser = getUserByEmail(userIdentifier);

        // Vérifier que la commande appartient à l'utilisateur
        // Comparer par ID d'utilisateur plutôt que par email
        if (!order.getUser().getId().equals(authenticatedUser.getId())) {
            log.warn("Tentative d'accès non autorisé: user de la commande = {}, user authentifié = {}",
                    order.getUser().getUsername(), authenticatedUser.getUsername());
            throw new AuthenticationException("Vous n'êtes pas autorisé à accéder à cette commande");
        }

        // Si la commande est déjà complétée ou échouée, retourner directement
        if (order.getPaymentStatus() == PaymentStatusType.COMPLETED ||
                order.getPaymentStatus() == PaymentStatusType.FAILED) {
            return mapToOrderResponse(order);
        }

        try {
            // Vérifier le statut du paiement avec Stripe
            PaymentStatusType paymentStatus = paymentService.checkPaymentStatus(sessionId);

            if (paymentStatus == PaymentStatusType.COMPLETED) {
                // Paiement réussi
                completeSuccessfulPayment(order);
                // Envoyer les billets par email
                ticketDeliveryService.deliverTickets(order);
                log.info("Paiement réussi et billets envoyés pour la commande: {}", order.getOrderReference());
            } else if (paymentStatus == PaymentStatusType.FAILED) {
                // Paiement échoué
                failPayment(order);
                log.info("Paiement échoué pour la commande: {}", order.getOrderReference());
            }
            // Si pending, on ne fait rien

            // Sauvegarder les changements
            order = ticketOrderRepository.save(order);

        } catch (Exception e) {
            log.error("Erreur lors de la vérification du paiement: {}", e.getMessage());
            throw new PaymentException("Erreur lors de la vérification du paiement: " + e.getMessage(), e);
        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(String userEmail) {
        User user = getUserByEmail(userEmail);
        List<TicketOrder> orders = ticketOrderRepository.findByUserOrderByCreatedAtDesc(user);

        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, String userEmail) {
        User user = getUserByEmail(userEmail);
        TicketOrder order = ticketOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'id: " + orderId));

        // Vérifier que la commande appartient à l'utilisateur
        if (!order.getUser().getId().equals(user.getId())) {
            throw new AuthenticationException("Vous n'êtes pas autorisé à accéder à cette commande");
        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public void cancelReservation(List<Long> ticketIds, String userEmail) {
        User user = getUserByEmail(userEmail);
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);

        for (Ticket ticket : tickets) {
            if (ticket.getStatus() != TicketStatusType.RESERVED) {
                throw new IllegalStateException("Le billet avec l'id " + ticket.getId() + " n'est pas réservé");
            }

            // Vérifier que le billet n'appartient pas à une commande
            if (ticket.getOrder() != null) {
                throw new IllegalStateException("Le billet avec l'id " + ticket.getId() + " fait partie d'une commande");
            }

            ticket.setStatus(TicketStatusType.AVAILABLE);
            ticket.setReservationTime(null);
            ticket.setExpirationTime(null);
        }

        ticketRepository.saveAll(tickets);

        // Mettre à jour les compteurs de billets disponibles
        for (Ticket ticket : tickets) {
            Match match = ticket.getMatch();
            Map<SectionType, Integer> availableTicketsMap = match.getAvailableTickets();
            int currentAvailable = availableTicketsMap.getOrDefault(ticket.getSectionType(), 0);
            availableTicketsMap.put(ticket.getSectionType(), currentAvailable + 1);
            matchRepository.save(match);
        }

        log.info("Réservation annulée pour {} billets par l'utilisateur: {}", tickets.size(), userEmail);
    }


    private void validateTicketsForPurchase(List<Ticket> tickets) {
        if (tickets.isEmpty()) {
            throw new IllegalArgumentException("Aucun billet trouvé pour l'achat");
        }

        for (Ticket ticket : tickets) {
            if (ticket.getStatus() != TicketStatusType.RESERVED) {
                throw new IllegalStateException("Le billet avec l'id " + ticket.getId() + " n'est pas réservé");
            }

            // Vérifier que la réservation n'a pas expiré
            if (ticket.getExpirationTime().isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("La réservation du billet avec l'id " + ticket.getId() + " a expiré");
            }

            // Vérifier que le billet n'est pas déjà dans une commande
            //if (ticket.getOrder() != null) {
                //throw new IllegalStateException("Le billet avec l'id " + ticket.getId() + " est déjà associé à une commande");
            //}
        }
    }

    private void completeSuccessfulPayment(TicketOrder order) {
        order.setPaymentStatus(PaymentStatusType.COMPLETED);
        order.setPaymentDate(LocalDateTime.now());

        // Mettre à jour le statut des billets
        for (Ticket ticket : order.getTickets()) {
            ticket.setStatus(TicketStatusType.SOLD);
            ticket.setReservationTime(null);
            ticket.setExpirationTime(null);
        }

        ticketRepository.saveAll(order.getTickets());
    }

    private void failPayment(TicketOrder order) {
        order.setPaymentStatus(PaymentStatusType.FAILED);

        // Libérer les billets
        releaseTickets(order.getTickets());
    }

    private void rollbackOrder(TicketOrder order) {
        // Supprimer la commande
        ticketOrderRepository.delete(order);

        // Libérer les billets
        releaseTickets(order.getTickets());
    }

    private void releaseTickets(List<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            ticket.setStatus(TicketStatusType.AVAILABLE);
            ticket.setReservationTime(null);
            ticket.setExpirationTime(null);
            ticket.setOrder(null);

            // Mettre à jour le compteur de billets disponibles
            Match match = ticket.getMatch();
            Map<SectionType, Integer> availableTicketsMap = match.getAvailableTickets();
            int currentAvailable = availableTicketsMap.getOrDefault(ticket.getSectionType(), 0);
            availableTicketsMap.put(ticket.getSectionType(), currentAvailable + 1);
            matchRepository.save(match);
        }

        ticketRepository.saveAll(tickets);
    }

    private OrderResponse mapToOrderResponse(TicketOrder order) {
        List<TicketResponse> ticketResponses = order.getTickets().stream()
                .map(ticketMapper::toResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderReference(order.getOrderReference())
                .createdAt(order.getCreatedAt())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .paymentReference(order.getPaymentReference())
                .paymentDate(order.getPaymentDate())
                .tickets(ticketResponses)
                .build();
    }

    private User getUserByEmail(String emailOrUsername) {
        // Essayer d'abord par email
        Optional<User> userByEmail = userRepository.findByEmail(emailOrUsername);
        if (userByEmail.isPresent()) {
            return userByEmail.get();
        }

        // Si pas trouvé, essayer par username
        return userRepository.findByUsername(emailOrUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'identifiant: " + emailOrUsername));
    }

    private String generateTicketCode(Match match, SectionType sectionType) {
        return "CAN25-" + match.getId() + "-" +
                sectionType.name().charAt(0) + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}