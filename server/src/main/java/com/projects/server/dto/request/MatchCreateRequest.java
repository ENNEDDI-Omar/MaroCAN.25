package com.projects.server.dto.request;

import com.projects.server.domain.enums.CompetitionPhaseType;
import com.projects.server.domain.enums.GroupType;
import com.projects.server.domain.enums.StadiumType;
import com.projects.server.domain.enums.TeamType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchCreateRequest {

    @NotNull(message = "La date et l'heure sont requises")
    @Future(message = "La date doit être dans le futur")
    private LocalDateTime dateTime;

    @NotNull(message = "Le stade est requis")
    private StadiumType stadium;

    @NotNull(message = "L'équipe à domicile est requise")
    private TeamType homeTeam;

    @NotNull(message = "L'équipe à l'extérieur est requise")
    private TeamType awayTeam;

    @NotNull(message = "La phase de compétition est requise")
    private CompetitionPhaseType phase;

    private GroupType group;
}