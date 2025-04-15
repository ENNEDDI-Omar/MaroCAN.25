package com.projects.server.services.interfaces;

import com.projects.server.domain.entities.TicketOrder;
import com.projects.server.domain.enums.PaymentStatusType;
import com.projects.server.dto.response.PaymentInitResponse;

public interface PaymentService {

    PaymentInitResponse initiatePayment(TicketOrder order);

    PaymentStatusType checkPaymentStatus(String transactionId);
}