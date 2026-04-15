package com.intera.adegaunderground.entity;

import java.io.Serializable;
import java.util.Objects;

public class ItemEventoPK implements Serializable {

    private Integer evento;
    private Integer produto;

    public ItemEventoPK() {}

    public ItemEventoPK(Integer evento, Integer produto) {
        this.evento = evento;
        this.produto = produto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemEventoPK)) return false;
        ItemEventoPK that = (ItemEventoPK) o;
        return Objects.equals(evento, that.evento) &&
                Objects.equals(produto, that.produto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(evento, produto);
    }
}