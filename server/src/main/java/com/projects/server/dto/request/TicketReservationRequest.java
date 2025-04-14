package com.projects.server.dto.request;

import com.projects.server.domain.enums.SectionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketReservationRequest {

    @NotNull(message = "L'identifiant du match est requis")
    private Long matchId;

    @NotNull(message = "La section est requise")
    private SectionType sectionType;

    @NotNull(message = "Le nombre de billets est requis")
    @Min(value = 1, message = "Le nombre de billets doit être au moins 1")
    private Integer quantity;
}