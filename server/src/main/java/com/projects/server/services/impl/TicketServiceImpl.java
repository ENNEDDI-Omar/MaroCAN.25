package com.projects.server.services.impl;

import com.projects.server.domain.entities.Match;
import com.projects.server.domain.entities.Ticket;
import com.projects.server.domain.enums.SectionType;
import com.projects.server.domain.enums.TicketStatusType;
import com.projects.server.dto.request.TicketGenerationRequest;
import com.projects.server.dto.response.TicketResponse;
import com.projects.server.exceptions.ResourceNotFoundException;
import com.projects.server.mapper.TicketMapper;
import com.projects.server.repositories.MatchRepository;
import com.projects.server.repositories.TicketRepository;
import com.projects.server.services.interfaces.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final MatchRepository matchRepository;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional
    public List<TicketResponse> generateTickets(TicketGenerationRequest request) {
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match non trouvé avec l'id: " + request.getMatchId()));

        // Vérifier si le nombre de billets demandé est disponible
        Map<SectionType, Integer> availableTickets = match.getAvailableTickets();
        Integer currentAvailable = availableTickets.getOrDefault(request.getSectionType(), 0);

        if (currentAvailable < request.getNumberOfTickets()) {
            throw new IllegalStateException("Nombre insuffisant de billets disponibles pour cette section");
        }

        // Mettre à jour le nombre de billets disponibles
        availableTickets.put(request.getSectionType(), currentAvailable - request.getNumberOfTickets());
        matchRepository.save(match);

        // Générer les billets
        List<Ticket> tickets = new ArrayList<>();
        double price = match.getPriceForSection(request.getSectionType());

        for (int i = 0; i < request.getNumberOfTickets(); i++) {
            Ticket ticket = Ticket.builder()
                    .match(match)
                    .sectionType(request.getSectionType())
                    .price(price)
                    .status(TicketStatusType.AVAILABLE)
                    .build();
            tickets.add(ticket);
        }

        tickets = ticketRepository.saveAll(tickets);

        return tickets.stream()
                .map(ticketMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countAvailableTickets(Long matchId, SectionType sectionType) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match non trouvé avec l'id: " + matchId));

        return ticketRepository.countByMatchAndSectionTypeAndStatus(match, sectionType, TicketStatusType.AVAILABLE);
    }

    @Override
    @Transactional
    public void updateTicketAvailability(Long matchId, SectionType sectionType, int availableCount) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match non trouvé avec l'id: " + matchId));

        Map<SectionType, Integer> availableTickets = match.getAvailableTickets();
        availableTickets.put(sectionType, availableCount);
        matchRepository.save(match);
    }
}