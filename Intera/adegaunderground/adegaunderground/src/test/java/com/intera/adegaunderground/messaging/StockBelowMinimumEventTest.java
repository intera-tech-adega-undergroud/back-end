package com.intera.adegaunderground.messaging;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Protege o contrato JSON consumido pelo notification-service. */
class StockBelowMinimumEventTest {

    @Test
    void serializaComOsNomesDoContrato() {
        StockBelowMinimumEvent event = StockBelowMinimumEvent.of(7, "Pringles", -1, 10);

        String json = JsonMapper.builder().build().writeValueAsString(event);

        assertTrue(json.contains("\"eventId\":\"" + UUID.fromString(event.eventId()) + "\""), json);
        assertTrue(json.contains("\"productId\":7"), json);
        assertTrue(json.contains("\"productName\":\"Pringles\""), json);
        assertTrue(json.contains("\"currentQuantity\":-1"), json);
        assertTrue(json.contains("\"minimumQuantity\":10"), json);
        assertTrue(json.contains("\"occurredAt\":\"" + OffsetDateTime.parse(event.occurredAt()) + "\""), json);
    }
}
