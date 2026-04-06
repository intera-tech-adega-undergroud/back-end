package com.intera.adegaunderground.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_unidade_bebida")
public class UnidadeBebida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUnidadeBebida;
    private Integer mlInicial;
    private Integer mlAtual;

    @Enumerated(EnumType.STRING)
    private StatusBebida status; // Crie um Enum com ABERTO, FECHADO

    @ManyToOne
    @JoinColumn(name = "id_produto")
    private Produto produto;

    public UnidadeBebida() {}

    public Integer getIdUnidadeBebida() {
        return idUnidadeBebida;
    }

    public void setIdUnidadeBebida(Integer idUnidadeBebida) {
        this.idUnidadeBebida = idUnidadeBebida;
    }

    public Integer getMlInicial() {
        return mlInicial;
    }

    public void setMlInicial(Integer mlInicial) {
        this.mlInicial = mlInicial;
    }

    public Integer getMlAtual() {
        return mlAtual;
    }

    public void setMlAtual(Integer mlAtual) {
        this.mlAtual = mlAtual;
    }

    public StatusBebida getStatus() {
        return status;
    }

    public void setStatus(StatusBebida status) {
        this.status = status;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }
}
