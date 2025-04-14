package com.projects.server.repositories;

import com.projects.server.domain.entities.Match;
import com.projects.server.domain.enums.CompetitionPhaseType;
import com.projects.server.domain.enums.GroupType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByDateTimeAfterOrderByDateTimeAsc(LocalDateTime dateTime);

    List<Match> findByPhaseOrderByDateTimeAsc(CompetitionPhaseType phase);

    List<Match> findByGroupOrderByDateTimeAsc(GroupType group);
}