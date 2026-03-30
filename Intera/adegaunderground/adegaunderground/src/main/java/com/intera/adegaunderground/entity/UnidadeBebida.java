package com.intera.adegaunderground.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_unidade_bebida")
public class UnidadeBebida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUnidadeBebida;
    private String mlInicial;
    private Integer mlAtual;

    @Enumerated(EnumType.STRING)
    private StatusBebida status; // Crie um Enum com ABERTO, FECHADO

    @ManyToOne
    @JoinColumn(name = "id_produto")
    private Produto produto;

    public UnidadeBebida(Integer idUnidadeBebida, String mlInicial, Integer mlAtual, StatusBebida status, Produto produto) {
        this.idUnidadeBebida = idUnidadeBebida;
        this.mlInicial = mlInicial;
        this.mlAtual = mlAtual;
        this.status = status;
        this.produto = produto;
    }
}
