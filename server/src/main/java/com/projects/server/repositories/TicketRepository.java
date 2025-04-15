package com.projects.server.repositories;

import com.projects.server.domain.entities.Match;
import com.projects.server.domain.entities.Ticket;
import com.projects.server.domain.enums.SectionType;
import com.projects.server.domain.enums.TicketStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByMatchAndSectionTypeAndStatus(Match match, SectionType sectionType, TicketStatusType status);

    long countByMatchAndSectionTypeAndStatus(Match match, SectionType sectionType, TicketStatusType status);

    List<Ticket> findByStatusAndExpirationTimeBefore(TicketStatusType status, LocalDateTime expirationTime);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.match.id = :matchId AND t.order.user.email = :userEmail")
    int countByMatchAndUserEmail(@Param("matchId") Long matchId, @Param("userEmail") String userEmail);

    int countByStatus(TicketStatusType status);

    int countBySectionType(SectionType sectionType);

    int countByMatchAndStatus(Match match, TicketStatusType status);

    Optional<Ticket> findByTicketCode(String ticketCode);
}