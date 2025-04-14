package com.projects.server.dto.response;

import com.projects.server.domain.enums.PaymentStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private String orderReference;
    private LocalDateTime createdAt;
    private PaymentStatusType paymentStatus;
    private Double totalAmount;
    private String paymentReference;
    private LocalDateTime paymentDate;
    private List<TicketResponse> tickets;
}