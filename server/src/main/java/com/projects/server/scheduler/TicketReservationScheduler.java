package com.projects.server.scheduler;

import com.projects.server.domain.entities.Ticket;
import com.projects.server.domain.entities.Match;
import com.projects.server.domain.enums.SectionType;
import com.projects.server.domain.enums.TicketStatusType;
import com.projects.server.repositories.MatchRepository;
import com.projects.server.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TicketReservationScheduler {

    private final TicketRepository ticketRepository;
    private final MatchRepository matchRepository;

    @Scheduled(fixedRate = 60000) // Toutes les minutes
    @Transactional
    public void releaseExpiredReservations() {
        log.info("Vérification des réservations expirées");
        LocalDateTime now = LocalDateTime.now();

        List<Ticket> expiredReservations = ticketRepository.findByStatusAndExpirationTimeBefore(
                TicketStatusType.RESERVED, now);

        if (!expiredReservations.isEmpty()) {
            log.info("Libération de {} réservations expirées", expiredReservations.size());

            for (Ticket ticket : expiredReservations) {
                ticket.setStatus(TicketStatusType.AVAILABLE);
                ticket.setReservationTime(null);
                ticket.setExpirationTime(null);

                // Mettre à jour le compteur de billets disponibles
                Match match = ticket.getMatch();
                SectionType sectionType = ticket.getSectionType();
                Map<SectionType, Integer> availableTicketsMap = match.getAvailableTickets();
                int currentAvailable = availableTicketsMap.getOrDefault(sectionType, 0);
                availableTicketsMap.put(sectionType, currentAvailable + 1);
                matchRepository.save(match);
            }

            ticketRepository.saveAll(expiredReservations);
            log.info("Réservations expirées libérées avec succès");
        }
    }
}