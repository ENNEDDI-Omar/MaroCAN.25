package com.projects.server.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketPurchaseRequest {

    @NotEmpty(message = "La liste des identifiants de billets est requise")
    private List<Long> ticketIds;

    @NotBlank(message = "Les informations de paiement sont requises")
    private String paymentInfo = "ONLINE_PAYMENT";
}