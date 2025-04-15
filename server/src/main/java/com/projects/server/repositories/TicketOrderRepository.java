package com.projects.server.repositories;

import com.projects.server.domain.entities.TicketOrder;
import com.projects.server.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketOrderRepository extends JpaRepository<TicketOrder, Long> {

    List<TicketOrder> findByUserOrderByCreatedAtDesc(User user);

    Optional<TicketOrder> findByPaymentReference(String paymentReference);
}