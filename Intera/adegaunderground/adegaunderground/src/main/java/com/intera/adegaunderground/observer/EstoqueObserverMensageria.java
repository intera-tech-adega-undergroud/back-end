package com.intera.adegaunderground.observer;

import com.intera.adegaunderground.entity.CategoriaBebida;
import com.intera.adegaunderground.entity.Produto;
import com.intera.adegaunderground.messaging.EstoqueAbaixoMinimoEvent;
import com.intera.adegaunderground.messaging.StockBelowMinimumEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Observer que transforma "estoque abaixo do minimo" em evento de integracao.
 * Regra de negocio: so BEBIDA_UNIDADE e DIVERSOS geram alerta em tempo real
 * (fracionados ficam no relatorio diario; drinks e combos nao tem estoque proprio).
 */
@Component
public class EstoqueObserverMensageria implements EstoqueObserver {

    private final ApplicationEventPublisher eventPublisher;

    public EstoqueObserverMensageria(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void atualizar(Produto produto) {
        if (!geraAlertaEmTempoReal(produto)) {
            return;
        }

        StockBelowMinimumEvent payload = StockBelowMinimumEvent.of(
                produto.getIdProduto(),
                produto.getNome(),
                produto.getQtdUnidade(),
                produto.getQtdMinimo());

        eventPublisher.publishEvent(new EstoqueAbaixoMinimoEvent(payload));
    }

    static boolean geraAlertaEmTempoReal(Produto produto) {
        if (produto == null
                || produto.getIdProduto() == null
                || produto.getNome() == null
                || produto.getQtdUnidade() == null
                || produto.getQtdMinimo() == null
                || produto.getCategoria() == null) {
            return false;
        }
        CategoriaBebida categoria = produto.getCategoria().getCategoria();
        return categoria == CategoriaBebida.BEBIDA_UNIDADE || categoria == CategoriaBebida.DIVERSOS;
    }
}
