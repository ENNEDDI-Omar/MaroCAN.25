package com.projects.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    private double totalRevenue;
    private int totalOrdersCount;
    private int totalTicketsSold;
    private List<DailySalesResponse> dailySales;
    private Map<String, Double> revenueByMatchCategory;
    private List<TopSellingMatchResponse> topSellingMatches;
}