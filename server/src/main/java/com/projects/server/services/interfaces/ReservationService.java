package com.projects.server.services.interfaces;

import com.projects.server.dto.request.TicketPurchaseRequest;
import com.projects.server.dto.request.TicketReservationRequest;
import com.projects.server.dto.response.OrderResponse;
import com.projects.server.dto.response.PaymentInitResponse;
import com.projects.server.dto.response.TicketResponse;

import java.util.List;

public interface ReservationService {

    List<TicketResponse> reserveTickets(TicketReservationRequest request, String userEmail);

    PaymentInitResponse initiatePayment(TicketPurchaseRequest request, String userEmail);

    OrderResponse completePayment(String sessionId, String userEmail);

    OrderResponse purchaseTickets(TicketPurchaseRequest request, String userEmail);

    List<OrderResponse> getUserOrders(String userEmail);

    OrderResponse getOrderById(Long orderId, String userEmail);

    void cancelReservation(List<Long> ticketIds, String userEmail);
}