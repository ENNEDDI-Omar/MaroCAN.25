package com.projects.server.domain.entities;

import com.projects.server.domain.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StadiumType stadium;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamType homeTeam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamType awayTeam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompetitionPhaseType phase;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_group")
    private GroupType matchGroup;

    private Integer matchScore;

    @ElementCollection
    @CollectionTable(name = "match_available_tickets",
            joinColumns = @JoinColumn(name = "match_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "section_type")
    @Column(name = "available_tickets")
    private Map<SectionType, Integer> availableTickets = new HashMap<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Transient
    public String getMatchTitle() {
        return homeTeam.getDisplayName() + " vs " + awayTeam.getDisplayName();
    }

    @Transient
    public double getPriceForSection(SectionType sectionType) {
        return stadium.getPriceForSection(sectionType);
    }

    @PrePersist
    public void prePersist() {
        // Initialiser availableTickets s'il est null
        if (this.availableTickets == null) {
            this.availableTickets = new HashMap<>();
        }

        // Initialiser le nombre de billets disponibles pour chaque section
        for (SectionType sectionType : SectionType.values()) {
            if (!availableTickets.containsKey(sectionType)) {
                availableTickets.put(sectionType, getStadium().getCapacityForSection(sectionType));
            }
        }

        // Déterminer automatiquement le groupe si c'est un match de phase de groupe
        if (phase == CompetitionPhaseType.GROUP_STAGE && matchGroup == null) {
            // Vérifier si les deux équipes appartiennent au même groupe
            GroupType homeTeamGroup = GroupType.getGroupForTeam(homeTeam);
            GroupType awayTeamGroup = GroupType.getGroupForTeam(awayTeam);

            if (homeTeamGroup != null && homeTeamGroup == awayTeamGroup) {
                matchGroup = homeTeamGroup;
            }
        }
    }
}