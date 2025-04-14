package com.projects.server.dto.response;

import com.projects.server.domain.enums.CompetitionPhaseType;
import com.projects.server.domain.enums.GroupType;
import com.projects.server.domain.enums.SectionType;
import com.projects.server.domain.enums.StadiumType;
import com.projects.server.domain.enums.TeamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {

    private Long id;
    private LocalDateTime dateTime;
    private StadiumType stadium;
    private TeamType homeTeam;
    private TeamType awayTeam;
    private CompetitionPhaseType phase;
    private GroupType group;
    private Integer matchScore;
    private Map<SectionType, Integer> availableTickets;
    private String matchTitle;
    private Map<SectionType, Double> sectionPrices;
}