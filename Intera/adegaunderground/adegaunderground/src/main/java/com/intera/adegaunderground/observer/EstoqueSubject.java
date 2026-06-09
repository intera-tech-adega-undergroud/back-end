package com.intera.adegaunderground.observer;

import com.intera.adegaunderground.entity.Produto;

public interface EstoqueSubject {

    void adicionarObserver(EstoqueObserver observer);

    void removerObserver(EstoqueObserver observer);

    void notificarObservers(Produto produto);
}