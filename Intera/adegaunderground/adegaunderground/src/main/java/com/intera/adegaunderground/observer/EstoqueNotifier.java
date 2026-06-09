package com.intera.adegaunderground.observer;

import com.intera.adegaunderground.entity.Produto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EstoqueNotifier
        implements EstoqueSubject {

    private final List<EstoqueObserver> observers =
            new ArrayList<>();

    public EstoqueNotifier(
            EstoqueObserverConsole observer
    ) {

        observers.add(observer);
    }

    @Override
    public void adicionarObserver(
            EstoqueObserver observer
    ) {

        observers.add(observer);
    }

    @Override
    public void removerObserver(
            EstoqueObserver observer
    ) {

        observers.remove(observer);
    }

    @Override
    public void notificarObservers(
            Produto produto
    ) {

        for (EstoqueObserver observer : observers) {

            observer.atualizar(produto);
        }
    }
}