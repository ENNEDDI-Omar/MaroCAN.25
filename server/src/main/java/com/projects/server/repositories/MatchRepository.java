package com.projects.server.repositories;

import com.projects.server.domain.entities.Match;
import com.projects.server.domain.enums.CompetitionPhaseType;
import com.projects.server.domain.enums.GroupType;
import com.projects.server.domain.enums.StadiumType;
import com.projects.server.domain.enums.TeamType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByDateTimeAfterOrderByDateTimeAsc(LocalDateTime dateTime);

    List<Match> findByPhaseOrderByDateTimeAsc(CompetitionPhaseType phase);

    // Méthode pour vérifier si les matchs d'une phase sont déjà initialisés
    boolean existsByPhase(CompetitionPhaseType phase);

    List<Match> findByPhase(CompetitionPhaseType phase);

    List<Match> findByMatchGroupOrderByDateTimeAsc(GroupType groupType);

    List<Match> findByDateTimeBetweenOrderByDateTimeAsc(LocalDateTime start, LocalDateTime end);

    List<Match> findByHomeTeamOrAwayTeamOrderByDateTimeAsc(TeamType homeTeam, TeamType awayTeam);

    List<Match> findByStadiumOrderByDateTimeAsc(StadiumType stadium);


}