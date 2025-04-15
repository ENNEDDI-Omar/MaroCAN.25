package com.projects.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatisticsResponse {
    private int totalTickets;
    private int ticketsAvailable;
    private int ticketsReserved;
    private int ticketsSold;
    private Map<String, Integer> ticketsByCategory;
    private Map<String, Integer> ticketsByMatch;
    private Map<String, Double> occupancyRateByMatch;
}