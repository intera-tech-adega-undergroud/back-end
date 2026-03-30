package com.intera.adegaunderground.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tbl_combo")
public class Combo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCombo;

    @NotNull(message = "Combo é obrigatório")
    @ManyToOne
    @JoinColumn(name = "id_produto_pai")
    private Produto produtoPai; // O Produto do tipo 'COMBO'

    @NotNull(message = "Item é obrigatório")
    @ManyToOne
    @JoinColumn(name = "id_produto_filho")
    private Produto produtoFilho; // Os itens dentro do combo (ex: Gelo, Suco)
    @NotNull(message = "Quantidade é obrigatório")
    private Integer quantidade;

    public Integer getIdCombo() {
        return idCombo;
    }

    public void setIdCombo(Integer idCombo) {
        this.idCombo = idCombo;
    }

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
