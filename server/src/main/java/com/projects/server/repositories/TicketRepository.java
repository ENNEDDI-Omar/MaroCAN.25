package com.projects.server.repositories;

import com.projects.server.domain.entities.Match;
import com.projects.server.domain.entities.Ticket;
import com.projects.server.domain.enums.SectionType;
import com.projects.server.domain.enums.TicketStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByMatchAndSectionTypeAndStatus(Match match, SectionType sectionType, TicketStatusType status);

    long countByMatchAndSectionTypeAndStatus(Match match, SectionType sectionType, TicketStatusType status);

    List<Ticket> findByStatusAndExpirationTimeBefore(TicketStatusType status, LocalDateTime expirationTime);
}