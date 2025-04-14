package com.projects.server.dto.response;

import com.projects.server.domain.enums.SectionType;
import com.projects.server.domain.enums.TicketStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {

    private Long id;
    private String ticketCode;
    private Long matchId;
    private String matchTitle;
    private LocalDateTime matchDateTime;
    private SectionType sectionType;
    private Double price;
    private TicketStatusType status;
    private LocalDateTime reservationTime;
    private LocalDateTime expirationTime;
}