package com.projects.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailySalesResponse {
    private String date;
    private double revenue;
    private int ordersCount;
    private int ticketsCount;
}
