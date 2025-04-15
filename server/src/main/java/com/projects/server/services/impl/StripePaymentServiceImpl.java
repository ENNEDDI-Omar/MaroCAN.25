package com.projects.server.services.impl;

import com.projects.server.domain.entities.TicketOrder;
import com.projects.server.domain.enums.PaymentStatusType;
import com.projects.server.dto.response.PaymentInitResponse;
import com.projects.server.exceptions.PaymentException;
import com.projects.server.services.interfaces.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentServiceImpl implements PaymentService {

    @Value("${stripe.public-key}")
    private String publicKey;

    @Override
    public PaymentInitResponse initiatePayment(TicketOrder order) {
        try {
            // Créer une session de paiement Stripe
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:4200/payment/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("http://localhost:4200/payment/cancel?session_id={CHECKOUT_SESSION_ID}")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("mad")
                                                    .setUnitAmount((long) (order.getTotalAmount() * 100)) // Stripe utilise les centimes
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Commande de billets CAN 2025 #" + order.getOrderReference())
                                                                    .setDescription("Achat de " + order.getTickets().size() + " billets")
                                                                    .build())
                                                    .build())
                                    .build())
                    .build();

            Session session = Session.create(params);

            // Sauvegarder l'ID de session dans la commande
            order.setPaymentReference(session.getId());

            return new PaymentInitResponse(session.getId(), session.getUrl(), publicKey);

        } catch (StripeException e) {
            log.error("Erreur lors de l'initialisation du paiement Stripe", e);
            throw new PaymentException("Échec de l'initialisation du paiement: " + e.getMessage());
        }
    }

    @Override
    public PaymentStatusType checkPaymentStatus(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);

            switch (session.getPaymentStatus()) {
                case "paid":
                    return PaymentStatusType.COMPLETED;
                case "unpaid":
                    return PaymentStatusType.PENDING;
                default:
                    return PaymentStatusType.FAILED;
            }

        } catch (StripeException e) {
            log.error("Erreur lors de la vérification du statut de paiement", e);
            throw new PaymentException("Échec de la vérification du statut: " + e.getMessage());
        }
    }
}