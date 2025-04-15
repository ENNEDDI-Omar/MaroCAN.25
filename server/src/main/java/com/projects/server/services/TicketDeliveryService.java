package com.projects.server.services;

import com.projects.server.domain.entities.TicketOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketDeliveryService {

    private final EmailService emailService;
    private final PDFGeneratorService pdfGenerator;

    @Async
    public void deliverTickets(TicketOrder order) {
        try {
            log.info("Envoi des billets pour la commande: {}", order.getOrderReference());

            // Générer le PDF des billets
            byte[] pdfDocument = pdfGenerator.generateTicketsPDF(order);

            // Construire le contenu de l'email en HTML simple
            String emailContent =
                    "<html><body>" +
                            "<h1>Confirmation de vos billets - CAN 2025</h1>" +
                            "<p>Bonjour " + order.getUser().getUsername() + ",</p>" +
                            "<p>Nous vous remercions pour votre commande. Votre paiement a été confirmé avec succès.</p>" +
                            "<p>Détails de votre commande :</p>" +
                            "<ul>" +
                            "  <li>Référence : <strong>" + order.getOrderReference() + "</strong></li>" +
                            "  <li>Date d'achat : <strong>" + order.getPaymentDate() + "</strong></li>" +
                            "  <li>Nombre de billets : <strong>" + order.getTickets().size() + "</strong></li>" +
                            "  <li>Montant total : <strong>" + order.getTotalAmount() + " MAD</strong></li>" +
                            "</ul>" +
                            "<p>Vos billets sont joints à cet email au format PDF.</p>" +
                            "<p>Cordialement,<br>L'équipe de la CAN 2025</p>" +
                            "</body></html>";

            // Envoyer l'email avec les billets
            emailService.sendEmailWithAttachment(
                    order.getUser().getEmail(),
                    "Vos billets pour la CAN 2025 - Commande #" + order.getOrderReference(),
                    emailContent,
                    pdfDocument,
                    "Billets_CAN2025_" + order.getOrderReference() + ".pdf"
            );

            log.info("Billets envoyés avec succès pour la commande: {}", order.getOrderReference());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi des billets pour la commande: {}", order.getOrderReference(), e);
        }
    }
}