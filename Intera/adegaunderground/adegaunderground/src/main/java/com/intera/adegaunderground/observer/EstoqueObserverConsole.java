package com.intera.adegaunderground.observer;

import com.intera.adegaunderground.entity.Produto;
import org.springframework.stereotype.Component;

@Component
public class EstoqueObserverConsole
        implements EstoqueObserver {

    @Override
    public void atualizar(
            Produto produto
    ) {

        if (produto.getQtdUnidade() < 0) {

            System.out.println(
                    "\n[ALERTA ESTOQUE NEGATIVO] "
                            + produto.getNome()
                            + " | Quantidade: "
                            + produto.getQtdUnidade()
            );

        } else {

            System.out.println(
                    "\n[ALERTA ESTOQUE CRÍTICO] "
                            + produto.getNome()
                            + " | Quantidade: "
                            + produto.getQtdUnidade()
                            + " | Mínimo: "
                            + produto.getQtdMinimo()
            );
        }
    }
}