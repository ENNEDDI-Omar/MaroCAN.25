package com.projects.server.mapper;

import com.projects.server.domain.entities.Ticket;
import com.projects.server.dto.response.TicketResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketMapper {

    @Mapping(target = "matchId", source = "match.id")
    @Mapping(target = "matchTitle", expression = "java(ticket.getMatch().getMatchTitle())")
    @Mapping(target = "matchDateTime", source = "match.dateTime")
    TicketResponse toResponse(Ticket ticket);
}