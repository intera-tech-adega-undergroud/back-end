package com.intera.adegaunderground.messaging;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Contrato do evento de alerta de estoque (autossuficiente: o consumidor nao consulta este banco).
 * occurredAt segue ISO-8601 com offset.
 */
public record StockBelowMinimumEvent(
        String eventId,
        Integer productId,
        String productName,
        Integer currentQuantity,
        Integer minimumQuantity,
        String occurredAt
) {

    public static StockBelowMinimumEvent of(Integer productId,
                                            String productName,
                                            Integer currentQuantity,
                                            Integer minimumQuantity) {
        return new StockBelowMinimumEvent(
                UUID.randomUUID().toString(),
                productId,
                productName,
                currentQuantity,
                minimumQuantity,
                OffsetDateTime.now().toString());
    }
}
