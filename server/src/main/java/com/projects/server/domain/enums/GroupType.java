package com.projects.server.domain.enums;

import lombok.Getter;
import java.util.Arrays;
import java.util.List;

@Getter
public enum GroupType {
    GROUP_A("Groupe A", Arrays.asList(
            TeamType.MAROC,
            TeamType.MALI,
            TeamType.ZAMBIE,
            TeamType.TANZANIE
    )),
    GROUP_B("Groupe B", Arrays.asList(
            TeamType.EGYPTE,
            TeamType.AFRIQUE_DU_SUD,
            TeamType.ANGOLA,
            TeamType.ZIMBABWE
    )),
    GROUP_C("Groupe C", Arrays.asList(
            TeamType.NIGERIA,
            TeamType.TUNISIE,
            TeamType.OUGANDA,
            TeamType.COMORES
    )),
    GROUP_D("Groupe D", Arrays.asList(
            TeamType.SENEGAL,
            TeamType.RD_CONGO,
            TeamType.BENIN,
            TeamType.BOTSWANA
    )),
    GROUP_E("Groupe E", Arrays.asList(
            TeamType.ALGERIE,
            TeamType.BURKINA_FASO,
            TeamType.GUINEE_EQUATORIALE,
            TeamType.SOUDAN
    )),
    GROUP_F("Groupe F", Arrays.asList(
            TeamType.COTE_IVOIRE,
            TeamType.CAMEROUN,
            TeamType.GABON,
            TeamType.MOZAMBIQUE
    ));

    private final String displayName;
    private final List<TeamType> teams;

    GroupType(String displayName, List<TeamType> teams) {
        this.displayName = displayName;
        this.teams = teams;
    }

    public static GroupType getGroupForTeam(TeamType team) {
        for (GroupType group : GroupType.values()) {
            if (group.getTeams().contains(team)) {
                return group;
            }
        }
        return null;
    }
}