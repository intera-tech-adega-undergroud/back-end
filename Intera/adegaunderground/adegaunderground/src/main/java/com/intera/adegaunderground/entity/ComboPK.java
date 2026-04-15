package com.intera.adegaunderground.entity;

import java.io.Serializable;
import java.util.Objects;

public class ComboPK implements Serializable {

    private Integer produtoPai;
    private Integer produtoFilho;

    public ComboPK() {
    }

    public ComboPK(Integer produtoPai, Integer produtoFilho) {
        this.produtoPai = produtoPai;
        this.produtoFilho = produtoFilho;
    }

    public Integer getProdutoPai() {
        return produtoPai;
    }

    public void setProdutoPai(Integer produtoPai) {
        this.produtoPai = produtoPai;
    }

    public Integer getProdutoFilho() {
        return produtoFilho;
    }

    public void setProdutoFilho(Integer produtoFilho) {
        this.produtoFilho = produtoFilho;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComboPK comboPK = (ComboPK) o;
        return Objects.equals(produtoPai, comboPK.produtoPai) &&
                Objects.equals(produtoFilho, comboPK.produtoFilho);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produtoPai, produtoFilho);
    }
}