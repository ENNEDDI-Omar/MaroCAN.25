package com.projects.server.domain.entities;

import com.projects.server.domain.enums.PaymentStatusType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticket_orders")
public class TicketOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatusType paymentStatus;

    @Column(nullable = false)
    private Double totalAmount;

    private String paymentReference;

    private LocalDateTime paymentDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (orderReference == null) {
            orderReference = "ORD-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10).toUpperCase();
        }
        if (paymentStatus == null) {
            paymentStatus = PaymentStatusType.PENDING;
        }
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        ticket.setOrder(this);
    }

    public void removeTicket(Ticket ticket) {
        tickets.remove(ticket);
        ticket.setOrder(null);
    }

    public void calculateTotalAmount() {
        this.totalAmount = tickets.stream()
                .mapToDouble(Ticket::getPrice)
                .sum();
    }
}