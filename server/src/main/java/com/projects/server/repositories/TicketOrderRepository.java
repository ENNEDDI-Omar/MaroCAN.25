package com.projects.server.repositories;

import com.projects.server.domain.entities.TicketOrder;
import com.projects.server.domain.entities.User;
import com.projects.server.domain.enums.PaymentStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketOrderRepository extends JpaRepository<TicketOrder, Long> {

    List<TicketOrder> findByUserOrderByCreatedAtDesc(User user);

    Optional<TicketOrder> findByPaymentReference(String paymentReference);

    List<TicketOrder> findByPaymentStatusAndCreatedAtBetweenOrderByCreatedAtDesc(
            PaymentStatusType status, LocalDateTime start, LocalDateTime end);

    List<TicketOrder> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime start, LocalDateTime end);

    List<TicketOrder> findAllByOrderByCreatedAtDesc();
}