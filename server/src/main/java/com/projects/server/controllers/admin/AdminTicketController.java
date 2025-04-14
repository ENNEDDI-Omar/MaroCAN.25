package com.projects.server.controllers.admin;

import com.projects.server.domain.enums.SectionType;
import com.projects.server.dto.request.TicketGenerationRequest;
import com.projects.server.dto.response.TicketResponse;
import com.projects.server.services.interfaces.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tickets")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTicketController {

    private final TicketService ticketService;

    @PostMapping("/generate")
    public ResponseEntity<List<TicketResponse>> generateTickets(@Valid @RequestBody TicketGenerationRequest request) {
        return ResponseEntity.ok(ticketService.generateTickets(request));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countAvailableTickets(
            @RequestParam Long matchId,
            @RequestParam SectionType sectionType) {
        return ResponseEntity.ok(ticketService.countAvailableTickets(matchId, sectionType));
    }

    @PutMapping("/availability")
    public ResponseEntity<Void> updateTicketAvailability(
            @RequestParam Long matchId,
            @RequestParam SectionType sectionType,
            @RequestParam int availableCount) {
        ticketService.updateTicketAvailability(matchId, sectionType, availableCount);
        return ResponseEntity.ok().build();
    }
}