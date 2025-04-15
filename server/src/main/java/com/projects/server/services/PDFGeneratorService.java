package com.projects.server.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.projects.server.domain.entities.Ticket;
import com.projects.server.domain.entities.TicketOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class PDFGeneratorService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generateTicketsPDF(TicketOrder order) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // En-tête du document
            document.add(new Paragraph("BILLETS OFFICIELS - CAN 2025").setBold().setFontSize(20));
            document.add(new Paragraph("Référence commande: " + order.getOrderReference()));
            document.add(new Paragraph("Date d'achat: " + order.getPaymentDate().format(DATE_TIME_FORMATTER)));

            // Pour chaque billet dans la commande
            for (Ticket ticket : order.getTickets()) {
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("BILLET: " + ticket.getTicketCode()).setBold());

                // Tableau avec les détails du billet
                Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}));

                table.addCell("Match");
                table.addCell(ticket.getMatch().getHomeTeam().getDisplayName() + " vs " +
                        ticket.getMatch().getAwayTeam().getDisplayName());

                table.addCell("Date et Heure");
                table.addCell(ticket.getMatch().getDateTime().format(DATE_TIME_FORMATTER));

                table.addCell("Stade");
                table.addCell(ticket.getMatch().getStadium().getName() + ", " +
                        ticket.getMatch().getStadium().getCity());

                table.addCell("Catégorie");
                table.addCell(ticket.getSectionType().name());

                table.addCell("Prix");
                table.addCell(ticket.getPrice() + " MAD");

                document.add(table);
                document.add(new Paragraph("\n"));
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF des billets", e);
            throw new RuntimeException("Erreur lors de la génération du PDF: " + e.getMessage(), e);
        }
    }
}