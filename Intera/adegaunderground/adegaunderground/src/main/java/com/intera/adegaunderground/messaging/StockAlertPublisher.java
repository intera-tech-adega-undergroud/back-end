package com.intera.adegaunderground.messaging;

/**
 * Porta de saida: publica o alerta de estoque para quem quiser consumir.
 * O dominio e o EventoService nao sabem qual broker existe atras desta interface.
 */
public interface StockAlertPublisher {

    void publish(StockBelowMinimumEvent event);
}
