package com.projects.server.domain.enums;

import lombok.Getter;

@Getter
public enum TeamType {
    MAROC("Maroc", "MA"),
    MALI("Mali", "ML"),
    ZAMBIE("Zambie", "ZM"),
    TANZANIE("Tanzanie", "TZ"),

    EGYPTE("Égypte", "EG"),
    AFRIQUE_DU_SUD("Afrique du Sud", "ZA"),
    ANGOLA("Angola", "AO"),
    ZIMBABWE("Zimbabwe", "ZW"),

    NIGERIA("Nigeria", "NG"),
    TUNISIE("Tunisie", "TN"),
    OUGANDA("Ouganda", "UG"),
    COMORES("Comores", "KM"),

    SENEGAL("Sénégal", "SN"),
    RD_CONGO("RD Congo", "CD"),
    BENIN("Bénin", "BJ"),
    BOTSWANA("Botswana", "BW"),

    ALGERIE("Algérie", "DZ"),
    BURKINA_FASO("Burkina Faso", "BF"),
    GUINEE_EQUATORIALE("Guinée équatoriale", "GQ"),
    SOUDAN("Soudan", "SD"),

    COTE_IVOIRE("Côte d'Ivoire", "CI"),
    CAMEROUN("Cameroun", "CM"),
    GABON("Gabon", "GA"),
    MOZAMBIQUE("Mozambique", "MZ");

    private final String displayName;
    private final String countryCode;

    TeamType(String displayName, String countryCode) {
        this.displayName = displayName;
        this.countryCode = countryCode;
    }

    public String getFlagUrl() {
        return "/assets/images/flags/" + this.name().toLowerCase() + ".png";
    }
}