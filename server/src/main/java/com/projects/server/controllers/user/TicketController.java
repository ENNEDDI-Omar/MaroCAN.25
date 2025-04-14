package com.projects.server.controllers.user;

import com.projects.server.dto.request.TicketPurchaseRequest;
import com.projects.server.dto.request.TicketReservationRequest;
import com.projects.server.dto.response.MatchResponse;
import com.projects.server.dto.response.OrderResponse;
import com.projects.server.dto.response.TicketResponse;
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

    @GetMapping("/matches")
    public ResponseEntity<List<MatchResponse>> getUpcomingMatches() {
        return ResponseEntity.ok(matchService.getAllUpcomingMatches());
    }

    @GetMapping("/matches/{id}")
    public ResponseEntity<MatchResponse> getMatchById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    @PostMapping("/reserve")
    public ResponseEntity<List<TicketResponse>> reserveTickets(
            @Valid @RequestBody TicketReservationRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(reservationService.reserveTickets(request, userEmail));
    }

    @PostMapping("/purchase")
    public ResponseEntity<OrderResponse> purchaseTickets(
            @Valid @RequestBody TicketPurchaseRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(reservationService.purchaseTickets(request, userEmail));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getUserOrders(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(reservationService.getUserOrders(userEmail));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(reservationService.getOrderById(id, userEmail));
    }

    @PostMapping("/cancel-reservation")
    public ResponseEntity<Void> cancelReservation(
            @RequestBody List<Long> ticketIds,
            Authentication authentication) {
        String userEmail = authentication.getName();
        reservationService.cancelReservation(ticketIds, userEmail);
        return ResponseEntity.ok().build();
    }
}