package com.intera.adegaunderground.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

/**
 * Adapter RabbitMQ da porta StockAlertPublisher. Unico lugar do monolito que conhece o broker.
 * Publica com confirmacao do broker (publisher confirm) e ate 3 tentativas.
 */
@Component
public class RabbitStockAlertPublisher implements StockAlertPublisher {

    public static final String EXCHANGE = "adega.stock.events";
    public static final String ROUTING_KEY = "stock.below-minimum";

    private static final Logger log = LoggerFactory.getLogger(RabbitStockAlertPublisher.class);
    private static final int MAX_ATTEMPTS = 3;
    private static final long CONFIRM_TIMEOUT_MS = 2000;
    private static final long RETRY_PAUSE_MS = 500;

    private final RabbitTemplate rabbitTemplate;
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    public RabbitStockAlertPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(StockBelowMinimumEvent event) {
        byte[] body = jsonMapper.writeValueAsString(event).getBytes(StandardCharsets.UTF_8);
        Message message = MessageBuilder.withBody(body)
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setContentEncoding(StandardCharsets.UTF_8.name())
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setMessageId(event.eventId())
                .build();

        RuntimeException lastError = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                rabbitTemplate.invoke(operations -> {
                    operations.send(EXCHANGE, ROUTING_KEY, message);
                    operations.waitForConfirmsOrDie(CONFIRM_TIMEOUT_MS);
                    return null;
                });
                log.info("stock_alert_published eventId={} productId={} quantity={} attempt={}",
                        event.eventId(), event.productId(), event.currentQuantity(), attempt);
                return;
            } catch (RuntimeException e) {
                lastError = e;
                log.warn("stock_alert_publish_failed eventId={} attempt={}/{} error=\"{}\"",
                        event.eventId(), attempt, MAX_ATTEMPTS, e.getMessage());
                pause();
            }
        }
        throw new IllegalStateException("Nao foi possivel publicar o alerta " + event.eventId(), lastError);
    }

    private static void pause() {
        try {
            Thread.sleep(RETRY_PAUSE_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
