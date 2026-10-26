package com.intera.adegaunderground.messaging;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Declara o exchange de eventos de estoque (mesmos parametros do notification-service: topic, duravel),
 * para que a publicacao nao falhe quando o consumidor ainda nao subiu.
 * Mensagens publicadas sem fila ligada ao exchange sao descartadas pelo RabbitMQ.
 */
@Configuration
@EnableAsync
public class RabbitMessagingConfig {

    @Bean
    public TopicExchange stockEventsExchange() {
        return new TopicExchange(RabbitStockAlertPublisher.EXCHANGE, true, false);
    }
}
