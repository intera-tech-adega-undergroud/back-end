package com.intera.adegaunderground.observer;

import com.intera.adegaunderground.entity.Categoria;
import com.intera.adegaunderground.entity.CategoriaBebida;
import com.intera.adegaunderground.entity.Produto;
import com.intera.adegaunderground.messaging.EstoqueAbaixoMinimoEvent;
import com.intera.adegaunderground.messaging.StockBelowMinimumEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class EstoqueObserverMensageriaTest {

    private ApplicationEventPublisher eventPublisher;
    private EstoqueObserverMensageria observer;

    @BeforeEach
    void setUp() {
        eventPublisher = mock(ApplicationEventPublisher.class);
        observer = new EstoqueObserverMensageria(eventPublisher);
    }

    private static Produto produto(CategoriaBebida categoria, int saldo, int minimo) {
        Categoria c = new Categoria();
        c.setCategoria(categoria);
        Produto p = new Produto();
        p.setIdProduto(4);
        p.setNome("Vodka Smirnoff 998ml");
        p.setQtdUnidade(saldo);
        p.setQtdMinimo(minimo);
        p.setCategoria(c);
        return p;
    }

    @ParameterizedTest
    @EnumSource(value = CategoriaBebida.class, names = {"BEBIDA_UNIDADE", "DIVERSOS"})
    void publicaEventoParaCategoriasComAlertaEmTempoReal(CategoriaBebida categoria) {
        observer.atualizar(produto(categoria, -2, 10));

        ArgumentCaptor<EstoqueAbaixoMinimoEvent> captor = ArgumentCaptor.forClass(EstoqueAbaixoMinimoEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        StockBelowMinimumEvent payload = captor.getValue().payload();
        assertEquals(4, payload.productId());
        assertEquals("Vodka Smirnoff 998ml", payload.productName());
        assertEquals(-2, payload.currentQuantity());
        assertEquals(10, payload.minimumQuantity());
        assertNotNull(payload.eventId());
        assertNotNull(payload.occurredAt());
    }

    @ParameterizedTest
    @EnumSource(value = CategoriaBebida.class, names = {"BEBIDA_FRACIONADA", "DRINK", "COMBO"})
    void naoPublicaParaFracionadosDrinksECombos(CategoriaBebida categoria) {
        observer.atualizar(produto(categoria, 0, 10));

        verify(eventPublisher, never()).publishEvent(any(Object.class));
    }

    @Test
    void ignoraProdutoSemCategoriaOuSemMinimo() {
        Produto semCategoria = produto(CategoriaBebida.DIVERSOS, 1, 5);
        semCategoria.setCategoria(null);
        observer.atualizar(semCategoria);

        Produto semMinimo = produto(CategoriaBebida.DIVERSOS, 1, 5);
        semMinimo.setQtdMinimo(null);
        observer.atualizar(semMinimo);

        verify(eventPublisher, never()).publishEvent(any(Object.class));
    }
}
