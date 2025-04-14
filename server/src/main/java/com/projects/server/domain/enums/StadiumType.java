package com.projects.server.domain.enums;

import lombok.Getter;

@Getter
public enum StadiumType {
    MOHAMMED_V_CASABLANCA("Stade Mohammed V", "Casablanca", 67000, 500.0),
    GRAND_STADE_AGADIR("Grand Stade d'Agadir", "Agadir", 45480, 400.0),
    STADE_FES("Stade de Fès", "Fès", 45000, 400.0),
    STADE_MARRAKECH("Grand Stade de Marrakech", "Marrakech", 45240, 450.0),
    MOULAY_ABDALLAH_RABAT("Stade Moulay Abdallah", "Rabat", 52000, 450.0),
    STADE_TANGER("Grand Stade de Tanger", "Tanger", 45000, 400.0);

    private final String name;
    private final String city;
    private final int capacity;
    private final double baseTicketPrice;

    StadiumType(String name, String city, int capacity, double baseTicketPrice) {
        this.name = name;
        this.city = city;
        this.capacity = capacity;
        this.baseTicketPrice = baseTicketPrice;
    }

    public String getImageUrl() {
        return "/assets/images/stadiums/" + this.name().toLowerCase() + ".jpg";
    }

    public int getCapacityForSection(SectionType sectionType) {
        return (int) Math.round(this.capacity * sectionType.getCapacityPercentage());
    }

    public double getPriceForSection(SectionType sectionType) {
        return this.baseTicketPrice * sectionType.getPriceMultiplier();
    }
}