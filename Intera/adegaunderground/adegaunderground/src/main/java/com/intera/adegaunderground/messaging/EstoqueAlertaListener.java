package com.intera.adegaunderground.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Decisao D2: o alerta so sai depois do commit (se a venda der rollback, nada e publicado)
 * e em outra thread, para a venda nunca esperar o broker nem falhar por causa dele.
 */
@Component
public class EstoqueAlertaListener {

    private static final Logger log = LoggerFactory.getLogger(EstoqueAlertaListener.class);

    private final StockAlertPublisher publisher;

    public EstoqueAlertaListener(StockAlertPublisher publisher) {
        this.publisher = publisher;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aoConfirmarVenda(EstoqueAbaixoMinimoEvent evento) {
        try {
            publisher.publish(evento.payload());
        } catch (RuntimeException e) {
            // Nunca propagar: a venda ja foi confirmada. O estoque critico tambem aparece no relatorio diario.
            log.error("stock_alert_lost eventId={} productId={} error=\"{}\"",
                    evento.payload().eventId(), evento.payload().productId(), e.getMessage());
        }
    }
}
