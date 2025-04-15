package com.projects.server.controllers.user;

import com.projects.server.dto.request.TicketReservationRequest;
import com.projects.server.dto.response.TicketResponse;
import com.projects.server.dto.response.MatchResponse;
import com.projects.server.services.interfaces.MatchService;
import com.projects.server.services.interfaces.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final MatchService matchService;
    private final ReservationService reservationService;

    /**
     * Récupère la liste des matchs à venir pour l'achat de billets
     * @return Liste des matchs disponibles
     */
    @GetMapping("/matches")
    public ResponseEntity<List<MatchResponse>> getUpcomingMatches() {
        return ResponseEntity.ok(matchService.getAllUpcomingMatches());
    }

    /**
     * Récupère les détails d'un match spécifique par son ID
     * @param id ID du match
     * @return Détails du match
     */
    @GetMapping("/matches/{id}")
    public ResponseEntity<MatchResponse> getMatchById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    /**
     * Réserve des billets pour un match
     * @param request Détails de la réservation (match, section, quantité)
     * @param authentication Informations d'authentification de l'utilisateur
     * @return Liste des billets réservés
     */
    @PostMapping("/reserve")
    public ResponseEntity<List<TicketResponse>> reserveTickets(
            @Valid @RequestBody TicketReservationRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(reservationService.reserveTickets(request, userEmail));
    }

    /**
     * Annule une réservation de billets
     * @param ticketIds Liste des IDs de billets à annuler
     * @param authentication Informations d'authentification de l'utilisateur
     * @return Réponse vide avec statut 200 (OK)
     */
    @PostMapping("/cancel-reservation")
    public ResponseEntity<Void> cancelReservation(
            @RequestBody List<Long> ticketIds,
            Authentication authentication) {
        String userEmail = authentication.getName();
        reservationService.cancelReservation(ticketIds, userEmail);
        return ResponseEntity.ok().build();
    }
}