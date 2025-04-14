package com.projects.server.services.impl;

@Service
@RequiredArgsConstructor
public class StripePaymentService implements PaymentService {

    @Override
    public PaymentInitResponse initiatePayment(TicketOrder order) {
        try {
            // Créer une session de paiement
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("https://marocan25.com/payment/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("https://marocan25.com/payment/cancel?session_id={CHECKOUT_SESSION_ID}")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(order.getQuantity().longValue())
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("mad")
                                                    .setUnitAmount((long) (order.getTotalAmount() * 100 / order.getQuantity()))
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Billet CAN 2025 - " + order.getMatchTitle())
                                                                    .setDescription("Section: " + order.getSectionType().name())
                                                                    .build())
                                                    .build())
                                    .build())
                    .build();

            Session session = Session.create(params);

            // Sauvegarder l'ID de session pour le traitement ultérieur
            order.setTransactionId(session.getId());

            return new PaymentInitResponse(session.getId(), session.getUrl());

        } catch (StripeException e) {
            throw new PaymentException("Erreur lors de l'initialisation du paiement: " + e.getMessage());
        }
    }

    @Override
    public PaymentStatusType checkPaymentStatus(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            if ("complete".equals(session.getStatus())) {
                return PaymentStatusType.COMPLETED;
            } else if ("expired".equals(session.getStatus())) {
                return PaymentStatusType.FAILED;
            } else {
                return PaymentStatusType.PENDING;
            }
        } catch (StripeException e) {
            throw new PaymentException("Erreur lors de la vérification du statut: " + e.getMessage());
        }
    }
}
