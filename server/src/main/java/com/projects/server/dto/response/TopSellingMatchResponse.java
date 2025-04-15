package com.projects.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingMatchResponse
{
    private Long matchId;
    private String matchTitle;
    private String date;
    private String stadium;
    private int ticketsSold;
    private double revenue;
}
