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
import com.projects.server.dto.response.TicketResponse;
import com.projects.server.exceptions.ResourceNotFoundException;
import com.projects.server.exceptions.AuthenticationException;
import com.projects.server.mapper.TicketMapper;
import com.projects.server.repositories.MatchRepository;
import com.projects.server.repositories.TicketOrderRepository;
import com.projects.server.repositories.TicketRepository;
import com.projects.server.repositories.UserRepository;
import com.projects.server.services.interfaces.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final TicketRepository ticketRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final TicketOrderRepository ticketOrderRepository;
    private final TicketMapper ticketMapper;

    // Durée de réservation en minutes
    private static final int RESERVATION_DURATION_MINUTES = 30;

    @Override
    @Transactional
    public List<TicketResponse> reserveTickets(TicketReservationRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match non trouvé avec l'id: " + request.getMatchId()));

        // Vérifier la disponibilité des billets
        List<Ticket> availableTickets = ticketRepository.findByMatchAndSectionTypeAndStatus(
                match, request.getSectionType(), TicketStatusType.AVAILABLE);

        if (availableTickets.size() < request.getQuantity()) {
            throw new IllegalStateException("Nombre insuffisant de billets disponibles");
        }

        // Sélectionner les billets à réserver
        List<Ticket> ticketsToReserve = availableTickets.subList(0, request.getQuantity());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(RESERVATION_DURATION_MINUTES);

        // Mettre à jour le statut des billets
        for (Ticket ticket : ticketsToReserve) {
            ticket.setStatus(TicketStatusType.RESERVED);
            ticket.setReservationTime(now);
            ticket.setExpirationTime(expirationTime);
        }

        ticketsToReserve = ticketRepository.saveAll(ticketsToReserve);

        // Mettre à jour le compteur de billets disponibles
        Map<SectionType, Integer> availableTicketsMap = match.getAvailableTickets();
        int currentAvailable = availableTicketsMap.getOrDefault(request.getSectionType(), 0);
        availableTicketsMap.put(request.getSectionType(), currentAvailable - request.getQuantity());
        matchRepository.save(match);

        return ticketsToReserve.stream()
                .map(ticketMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse purchaseTickets(TicketPurchaseRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);

        // Récupérer les billets réservés
        List<Ticket> tickets = ticketRepository.findAllById(request.getTicketIds());

        // Vérifier que tous les billets sont réservés
        for (Ticket ticket : tickets) {
            if (ticket.getStatus() != TicketStatusType.RESERVED) {
                throw new IllegalStateException("Le billet avec l'id " + ticket.getId() + " n'est pas réservé");
            }

            // Vérifier que la réservation n'a pas expiré
            if (ticket.getExpirationTime().isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("La réservation du billet avec l'id " + ticket.getId() + " a expiré");
            }
        }

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
            ticket.setStatus(TicketStatusType.SOLD);
        }

        order.calculateTotalAmount();

        // Simuler le processus de paiement
        // Dans un système réel, vous intégreriez ici une passerelle de paiement
        boolean paymentSuccess = processPayment(order, request.getPaymentInfo());

        if (paymentSuccess) {
            order.setPaymentStatus(PaymentStatusType.COMPLETED);
            order.setPaymentDate(LocalDateTime.now());
            order.setPaymentReference("PAY-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase());
        } else {
            order.setPaymentStatus(PaymentStatusType.FAILED);
            // Remettre les billets en disponible
            for (Ticket ticket : tickets) {
                ticket.setStatus(TicketStatusType.AVAILABLE);
                ticket.setReservationTime(null);
                ticket.setExpirationTime(null);
                ticket.setOrder(null);
            }
            throw new IllegalStateException("Le paiement a échoué");
        }

        order = ticketOrderRepository.save(order);

        // Créer la réponse
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

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email: " + email));
    }

    private boolean processPayment(TicketOrder order, String paymentInfo) {
        // Simulation d'un processus de paiement
        // Dans un système réel, vous intégreriez ici une passerelle de paiement
        return true;
    }
}