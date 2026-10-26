package com.intera.adegaunderground.messaging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EstoqueAlertaListenerTest {

    private static EstoqueAbaixoMinimoEvent evento() {
        return new EstoqueAbaixoMinimoEvent(StockBelowMinimumEvent.of(1, "Heineken", 3, 10));
    }

    @Test
    void delegaOEventoParaAPortaDePublicacao() {
        StockAlertPublisher publisher = mock(StockAlertPublisher.class);
        EstoqueAbaixoMinimoEvent evento = evento();

        new EstoqueAlertaListener(publisher).aoConfirmarVenda(evento);

        verify(publisher).publish(evento.payload());
    }

    @Test
    void falhaNaPublicacaoNuncaPropagaParaAVenda() {
        StockAlertPublisher publisher = mock(StockAlertPublisher.class);
        doThrow(new IllegalStateException("broker fora")).when(publisher).publish(any());

        assertDoesNotThrow(() -> new EstoqueAlertaListener(publisher).aoConfirmarVenda(evento()));
    }
}
