package com.intera.adegaunderground.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tbl_combo")
public class Combo {

    @Id
    @ManyToOne
    @JoinColumn(name = "tbl_produto_id_combo")
    private Produto produtoPai;

    @Id
    @ManyToOne
    @JoinColumn(name = "tbl_produto_id_produto")
    private Produto produtoFilho;

    @NotNull(message = "Quantidade é obrigatório")
    @Column(name = "quantidade_produto")
    private Integer quantidade;

    public Produto getProdutoPai() {
        return produtoPai;
    }

    public void setProdutoPai(Produto produtoPai) {
        this.produtoPai = produtoPai;
    }

    public Produto getProdutoFilho() {
        return produtoFilho;
    }

    public void setProdutoFilho(Produto produtoFilho) {
        this.produtoFilho = produtoFilho;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}