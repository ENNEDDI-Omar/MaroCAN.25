package com.projects.server.services.interfaces;

import com.projects.server.domain.enums.SectionType;
import com.projects.server.dto.request.TicketGenerationRequest;
import com.projects.server.dto.response.TicketResponse;
import com.projects.server.dto.response.TicketStatisticsResponse;

import java.util.List;

public interface TicketService {

    List<TicketResponse> generateTickets(TicketGenerationRequest request);

    long countAvailableTickets(Long matchId, SectionType sectionType);

    void updateTicketAvailability(Long matchId, SectionType sectionType, int availableCount);

    //TicketStatisticsResponse getTicketStatistics();
}