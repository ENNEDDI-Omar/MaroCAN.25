package com.projects.server.domain.entities;

import com.projects.server.domain.enums.SectionType;
import com.projects.server.domain.enums.TicketStatusType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticketCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SectionType sectionType;

    @Column(nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatusType status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private TicketOrder order;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime reservationTime;

    private LocalDateTime expirationTime;

    @PrePersist
    public void prePersist() {
        if (ticketCode == null) {
            String shortUuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
            this.ticketCode = "CAN25-" + match.getId() + "-" + sectionType.name().charAt(0) + shortUuid;
        }
        if (status == null) {
            status = TicketStatusType.AVAILABLE;
        }
        if (price == null && match != null) {
            price = match.getPriceForSection(sectionType);
        }
    }
}