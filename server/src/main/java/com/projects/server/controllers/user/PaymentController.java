package com.projects.server.controllers;

import com.projects.server.dto.request.TicketPurchaseRequest;
import com.projects.server.dto.response.OrderResponse;
import com.projects.server.dto.response.PaymentInitResponse;
import com.projects.server.services.interfaces.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final ReservationService reservationService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentInitResponse> initiatePayment(
            @Valid @RequestBody TicketPurchaseRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(reservationService.initiatePayment(request, userEmail));
    }

    @GetMapping("/complete")
    public ResponseEntity<OrderResponse> completePayment(
            @RequestParam String sessionId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(reservationService.completePayment(sessionId, userEmail));
    }
}