package com.projects.server.controllers.user;

import com.projects.server.domain.entities.TicketOrder;
import com.projects.server.exceptions.AuthenticationException;
import com.projects.server.exceptions.ResourceNotFoundException;
import com.projects.server.repositories.TicketOrderRepository;
import com.projects.server.repositories.UserRepository;
import com.projects.server.services.PDFGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class TicketPDFController {

    private final TicketOrderRepository ticketOrderRepository;
    private final PDFGeneratorService pdfGeneratorService;

    @GetMapping("/{orderId}/tickets/pdf")
    public ResponseEntity<byte[]> downloadTicketsPDF(
            @PathVariable Long orderId,
            Authentication authentication) {
        // Récupérer la commande
        TicketOrder order = ticketOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'id: " + orderId));

        // Vérifier que l'utilisateur authentifié est le propriétaire de la commande
        String userEmail = authentication.getName();
        if (!order.getUser().getUsername().equals(userEmail) && !order.getUser().getEmail().equals(userEmail)) {
            throw new AuthenticationException("Vous n'êtes pas autorisé à accéder à cette commande");
        }

        // Générer le PDF
        byte[] pdfContent = pdfGeneratorService.generateTicketsPDF(order);

        // Configurer les en-têtes HTTP pour le téléchargement
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Billets_CAN2025_" + order.getOrderReference() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }
}