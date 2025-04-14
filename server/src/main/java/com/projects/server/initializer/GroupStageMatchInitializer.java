package com.projects.server.initializer;

import com.projects.server.domain.entities.Match;
import com.projects.server.domain.enums.*;
import com.projects.server.repositories.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class GroupStageMatchInitializer {

    private final MatchRepository matchRepository;

    @Transactional
    public void initializeGroupStageMatches() {
        // Vérifier si les matchs sont déjà initialisés
        if (matchRepository.existsByPhase(CompetitionPhaseType.GROUP_STAGE)) {
            log.info("Les matchs de la phase de groupes sont déjà initialisés");
            return;
        }

        List<Match> groupStageMatches = new ArrayList<>();

        // Groupe A
        groupStageMatches.addAll(createGroupAMatches());

        // Groupe B
        groupStageMatches.addAll(createGroupBMatches());

        // Groupe C
        groupStageMatches.addAll(createGroupCMatches());

        // Groupe D
        groupStageMatches.addAll(createGroupDMatches());

        // Groupe E
        groupStageMatches.addAll(createGroupEMatches());

        // Groupe F
        groupStageMatches.addAll(createGroupFMatches());

        // Sauvegarder tous les matchs
        matchRepository.saveAll(groupStageMatches);
        log.info("Initialisation de {} matchs de la phase de groupes terminée", groupStageMatches.size());
    }

    private List<Match> createGroupAMatches() {
        return List.of(
                // Match 1
                createMatch(
                        TeamType.MAROC, TeamType.COMORES,
                        StadiumType.MOULAY_ABDALLAH_RABAT,
                        LocalDateTime.of(2025, 12, 21, 20, 0),
                        GroupType.GROUP_A
                ),
                // Match 2
                createMatch(
                        TeamType.MALI, TeamType.ZAMBIE,
                        StadiumType.MOHAMMED_V_CASABLANCA,
                        LocalDateTime.of(2025, 12, 22, 15, 30),
                        GroupType.GROUP_A
                ),
                // Match 13
                createMatch(
                        TeamType.MAROC, TeamType.MALI,
                        StadiumType.MOULAY_ABDALLAH_RABAT,
                        LocalDateTime.of(2025, 12, 26, 13, 0),
                        GroupType.GROUP_A
                ),
                // Match 14
                createMatch(
                        TeamType.ZAMBIE, TeamType.COMORES,
                        StadiumType.MOHAMMED_V_CASABLANCA,
                        LocalDateTime.of(2025, 12, 26, 15, 30),
                        GroupType.GROUP_A
                ),
                // Match 25
                createMatch(
                        TeamType.ZAMBIE, TeamType.MAROC,
                        StadiumType.MOULAY_ABDALLAH_RABAT,
                        LocalDateTime.of(2025, 12, 29, 18, 30),
                        GroupType.GROUP_A
                ),
                // Match 26
                createMatch(
                        TeamType.COMORES, TeamType.MALI,
                        StadiumType.MOHAMMED_V_CASABLANCA,
                        LocalDateTime.of(2025, 12, 29, 18, 30),
                        GroupType.GROUP_A
                )
        );
    }

    // Méthodes similaires pour les autres groupes (B, C, D, E, F)
    // Je n'ai montré que le Groupe A pour des raisons de concision

    private List<Match> createGroupBMatches() {
        return List.of(
                // Match 3
                createMatch(
                        TeamType.EGYPTE, TeamType.ZIMBABWE,
                        StadiumType.GRAND_STADE_AGADIR,
                        LocalDateTime.of(2025, 12, 22, 18, 0),
                        GroupType.GROUP_B
                ),
                // Match 4
                createMatch(
                        TeamType.AFRIQUE_DU_SUD, TeamType.ANGOLA,
                        StadiumType.STADE_MARRAKECH,
                        LocalDateTime.of(2025, 12, 22, 20, 30),
                        GroupType.GROUP_B
                ),
                // Match 15
                createMatch(
                        TeamType.EGYPTE, TeamType.AFRIQUE_DU_SUD,
                        StadiumType.GRAND_STADE_AGADIR,
                        LocalDateTime.of(2025, 12, 26, 18, 0),
                        GroupType.GROUP_B
                ),
                // Match 16
                createMatch(
                        TeamType.ZIMBABWE, TeamType.ANGOLA,
                        StadiumType.STADE_MARRAKECH,
                        LocalDateTime.of(2025, 12, 26, 20, 30),
                        GroupType.GROUP_B
                ),
                // Match 27
                createMatch(
                        TeamType.ANGOLA, TeamType.EGYPTE,
                        StadiumType.GRAND_STADE_AGADIR,
                        LocalDateTime.of(2025, 12, 29, 20, 30),
                        GroupType.GROUP_B
                ),
                // Match 28
                createMatch(
                        TeamType.ZIMBABWE, TeamType.AFRIQUE_DU_SUD,
                        StadiumType.STADE_MARRAKECH,
                        LocalDateTime.of(2025, 12, 29, 20, 30),
                        GroupType.GROUP_B
                )
        );
    }

    private List<Match> createGroupCMatches() {
        return List.of(
                // Match 5
                createMatch(
                        TeamType.NIGERIA, TeamType.TANZANIE,
                        StadiumType.STADE_FES,
                        LocalDateTime.of(2025, 12, 23, 13, 0),
                        GroupType.GROUP_C
                ),
                // Match 6
                createMatch(
                        TeamType.TUNISIE, TeamType.OUGANDA,
                        StadiumType.MOULAY_ABDALLAH_RABAT,
                        LocalDateTime.of(2025, 12, 23, 15, 30),
                        GroupType.GROUP_C
                ),
                // Match 17
                createMatch(
                        TeamType.NIGERIA, TeamType.TUNISIE,
                        StadiumType.STADE_FES,
                        LocalDateTime.of(2025, 12, 27, 13, 0),
                        GroupType.GROUP_C
                ),
                // Match 18
                createMatch(
                        TeamType.OUGANDA, TeamType.TANZANIE,
                        StadiumType.MOULAY_ABDALLAH_RABAT,
                        LocalDateTime.of(2025, 12, 27, 15, 30),
                        GroupType.GROUP_C
                ),
                // Match 29
                createMatch(
                        TeamType.OUGANDA, TeamType.NIGERIA,
                        StadiumType.STADE_FES,
                        LocalDateTime.of(2025, 12, 30, 18, 0),
                        GroupType.GROUP_C
                ),
                // Match 30
                createMatch(
                        TeamType.TANZANIE, TeamType.TUNISIE,
                        StadiumType.MOULAY_ABDALLAH_RABAT,
                        LocalDateTime.of(2025, 12, 30, 18, 0),
                        GroupType.GROUP_C
                )
        );
    }

    private List<Match> createGroupDMatches() {
        return List.of(
                // Match 7
                createMatch(
                        TeamType.SENEGAL, TeamType.BOTSWANA,
                        StadiumType.STADE_TANGER,
                        LocalDateTime.of(2025, 12, 23, 18, 0),
                        GroupType.GROUP_D
                ),
                // Match 8
                createMatch(
                        TeamType.RD_CONGO, TeamType.BENIN,
                        StadiumType.STADE_AL_BARID,
                        LocalDateTime.of(2025, 12, 23, 20, 30),
                        GroupType.GROUP_D
                ),
                // Match 19
                createMatch(
                        TeamType.SENEGAL, TeamType.RD_CONGO,
                        StadiumType.STADE_TANGER,
                        LocalDateTime.of(2025, 12, 27, 18, 0),
                        GroupType.GROUP_D
                ),
                // Match 20
                createMatch(
                        TeamType.BENIN, TeamType.BOTSWANA,
                        StadiumType.STADE_AL_BARID,
                        LocalDateTime.of(2025, 12, 27, 20, 30),
                        GroupType.GROUP_D
                ),
                // Match 31
                createMatch(
                        TeamType.BENIN, TeamType.SENEGAL,
                        StadiumType.STADE_TANGER,
                        LocalDateTime.of(2025, 12, 30, 20, 30),
                        GroupType.GROUP_D
                ),
                // Match 32
                createMatch(
                        TeamType.BOTSWANA, TeamType.RD_CONGO,
                        StadiumType.STADE_AL_BARID,
                        LocalDateTime.of(2025, 12, 30, 20, 30),
                        GroupType.GROUP_D
                )
        );
    }

    private List<Match> createGroupEMatches() {
        return List.of(
                // Match 9
                createMatch(
                        TeamType.ALGERIE, TeamType.SOUDAN,
                        StadiumType.STADE_MOULAY_HASSAN,
                        LocalDateTime.of(2025, 12, 24, 13, 0),
                        GroupType.GROUP_E
                ),
                // Match 10
                createMatch(
                        TeamType.BURKINA_FASO, TeamType.GUINEE_EQUATORIALE,
                        StadiumType.MOHAMMED_V_CASABLANCA,
                        LocalDateTime.of(2025, 12, 24, 15, 30),
                        GroupType.GROUP_E
                ),
                // Match 21
                createMatch(
                        TeamType.ALGERIE, TeamType.BURKINA_FASO,
                        StadiumType.STADE_MOULAY_HASSAN,
                        LocalDateTime.of(2025, 12, 28, 13, 0),
                        GroupType.GROUP_E
                ),
                // Match 22
                createMatch(
                        TeamType.GUINEE_EQUATORIALE, TeamType.SOUDAN,
                        StadiumType.MOHAMMED_V_CASABLANCA,
                        LocalDateTime.of(2025, 12, 28, 15, 30),
                        GroupType.GROUP_E
                ),
                // Match 33
                createMatch(
                        TeamType.GUINEE_EQUATORIALE, TeamType.ALGERIE,
                        StadiumType.STADE_MOULAY_HASSAN,
                        LocalDateTime.of(2025, 12, 31, 18, 0),
                        GroupType.GROUP_E
                ),
                // Match 34
                createMatch(
                        TeamType.SOUDAN, TeamType.BURKINA_FASO,
                        StadiumType.MOHAMMED_V_CASABLANCA,
                        LocalDateTime.of(2025, 12, 31, 18, 0),
                        GroupType.GROUP_E
                )
        );
    }

    private List<Match> createGroupFMatches() {
        return List.of(
                // Match 11
                createMatch(
                        TeamType.COTE_IVOIRE, TeamType.MOZAMBIQUE,
                        StadiumType.STADE_MARRAKECH,
                        LocalDateTime.of(2025, 12, 24, 18, 0),
                        GroupType.GROUP_F
                ),
                // Match 12
                createMatch(
                        TeamType.CAMEROUN, TeamType.GABON,
                        StadiumType.GRAND_STADE_AGADIR,
                        LocalDateTime.of(2025, 12, 24, 20, 30),
                        GroupType.GROUP_F
                ),
                // Match 23
                createMatch(
                        TeamType.COTE_IVOIRE, TeamType.CAMEROUN,
                        StadiumType.STADE_MARRAKECH,
                        LocalDateTime.of(2025, 12, 28, 18, 0),
                        GroupType.GROUP_F
                ),
                // Match 24
                createMatch(
                        TeamType.GABON, TeamType.MOZAMBIQUE,
                        StadiumType.GRAND_STADE_AGADIR,
                        LocalDateTime.of(2025, 12, 28, 20, 30),
                        GroupType.GROUP_F
                ),
                // Match 35
                createMatch(
                        TeamType.GABON, TeamType.COTE_IVOIRE,
                        StadiumType.STADE_MARRAKECH,
                        LocalDateTime.of(2025, 12, 31, 20, 30),
                        GroupType.GROUP_F
                ),
                // Match 36
                createMatch(
                        TeamType.MOZAMBIQUE, TeamType.CAMEROUN,
                        StadiumType.GRAND_STADE_AGADIR,
                        LocalDateTime.of(2025, 12, 31, 20, 30),
                        GroupType.GROUP_F
                )
        );
    }

    private Match createMatch(
            TeamType homeTeam,
            TeamType awayTeam,
            StadiumType stadium,
            LocalDateTime dateTime,
            GroupType group
    ) {
        return Match.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .stadium(stadium)
                .dateTime(dateTime)
                .phase(CompetitionPhaseType.GROUP_STAGE)
                .matchGroup(group)
                .build();
    }
}
