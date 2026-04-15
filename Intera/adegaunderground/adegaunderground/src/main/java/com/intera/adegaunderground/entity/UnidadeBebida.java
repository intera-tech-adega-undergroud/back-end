package com.intera.adegaunderground.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
@Table(name = "tbl_unidade_bebida")
@Schema(description = "Representa uma unidade física de bebida (ex: uma garrafa aberta com controle de ml)")
public class UnidadeBebida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID da unidade de bebida", example = "1")
    private Integer idUnidadeBebida;

    @Schema(description = "Volume inicial da bebida em ml", example = "750")
    private Integer mlInicial;

    @Schema(description = "Volume atual restante em ml", example = "500")
    private Integer mlAtual;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Status da unidade de bebida", example = "ABERTO")
    private StatusBebida status; // ABERTO, FECHADO

    @ManyToOne
    @JoinColumn(name = "id_produto")
    @Schema(description = "Produto relacionado à unidade de bebida")
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