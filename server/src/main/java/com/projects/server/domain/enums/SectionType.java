package com.projects.server.domain.enums;

import lombok.Getter;

@Getter
public enum SectionType {
    CATEGORY_1(0.15, 1.0),     // Premium - 15% de la capacité totale, prix standard
    CATEGORY_2(0.25, 0.75),    // 25% de la capacité, 75% du prix standard
    CATEGORY_3(0.35, 0.5),     // 35% de la capacité, 50% du prix standard
    CATEGORY_4(0.25, 0.25);    // 25% de la capacité, 25% du prix standard

    private final double capacityPercentage;
    private final double priceMultiplier;

    SectionType(double capacityPercentage, double priceMultiplier) {
        this.capacityPercentage = capacityPercentage;
        this.priceMultiplier = priceMultiplier;
    }

    public int calculateCapacity(int totalCapacity) {
        return (int) Math.round(totalCapacity * capacityPercentage);
    }

    public double calculatePrice(double basePrice) {
        return basePrice * priceMultiplier;
    }
}