package com.projects.server.controllers.user;

import com.projects.server.dto.request.TicketPurchaseRequest;
import com.projects.server.dto.response.OrderResponse;
import com.projects.server.dto.response.PaymentInitResponse;
import com.projects.server.services.interfaces.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class TicketOrderController {

    private final ReservationService reservationService;

    /**
     * Initialise le processus de paiement
     * @param request Détails de l'achat (IDs des billets réservés)
     * @param authentication Informations d'authentification de l'utilisateur
     * @return Informations pour redirection vers la page de paiement
     */
    @PostMapping("/checkout")
    public ResponseEntity<PaymentInitResponse> initiatePayment(
            @Valid @RequestBody TicketPurchaseRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(reservationService.initiatePayment(request, userEmail));
    }

    /**
     * Confirme et finalise une commande après paiement
     * @param sessionId ID de session de paiement
     * @param authentication Informations d'authentification de l'utilisateur
     * @return Détails de la commande finalisée
     */
    @GetMapping("/complete")
    public ResponseEntity<OrderResponse> completePayment(
            @RequestParam String sessionId,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            return ResponseEntity.ok(reservationService.completePayment(sessionId, userEmail));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Récupère toutes les commandes de l'utilisateur connecté
     * @param authentication Informations d'authentification de l'utilisateur
     * @return Liste des commandes de l'utilisateur
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(reservationService.getUserOrders(userEmail));
    }

    /**
     * Récupère les détails d'une commande spécifique
     * @param id ID de la commande
     * @param authentication Informations d'authentification de l'utilisateur
     * @return Détails de la commande
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(reservationService.getOrderById(id, userEmail));
    }


}