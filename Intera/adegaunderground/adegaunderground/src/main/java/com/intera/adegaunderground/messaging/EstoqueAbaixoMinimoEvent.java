package com.intera.adegaunderground.messaging;

/**
 * Evento interno do Spring, publicado durante a venda. So e entregue ao listener
 * depois do commit da transacao (ver EstoqueAlertaListener).
 */
public record EstoqueAbaixoMinimoEvent(StockBelowMinimumEvent payload) {
}
