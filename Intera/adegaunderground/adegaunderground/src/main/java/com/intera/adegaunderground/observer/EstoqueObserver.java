package com.intera.adegaunderground.observer;

import com.intera.adegaunderground.entity.Produto;

public interface EstoqueObserver {

    void atualizar(Produto produto);

}