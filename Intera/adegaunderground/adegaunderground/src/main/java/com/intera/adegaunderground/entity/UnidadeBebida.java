package com.intera.adegaunderground.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tbl_unidade_bebida")
@Schema(description = "Representa uma unidade física de bebida (ex: uma garrafa aberta com controle de ml)")
public class UnidadeBebida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID da unidade de bebida", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer idUnidadeBebida;

    @Schema(description = "Volume inicial da bebida em ml", example = "750")
    @NotNull @Positive
    private Integer mlInicial;

    @Schema(description = "Volume atual restante em ml", example = "500")
    @NotNull @PositiveOrZero
    private Integer mlAtual;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Status da unidade de bebida", example = "ABERTO")
    @NotNull
    private StatusBebida status; // ABERTO, FECHADO (lacrada), ESGOTADA

    @ManyToOne
    @JoinColumn(name = "id_produto")
    @Schema(description = "Produto relacionado à unidade de bebida")
    @NotNull
    private Produto produto;

    @JsonIgnore
    @AssertTrue(message = "Volume atual não pode ser maior que o volume inicial.")
    public boolean isVolumeValid() {
        return mlInicial == null || mlAtual == null || mlAtual <= mlInicial;
    }

    /** Controle de concorrência otimista (coluna versao). */
    @JsonIgnore
    @Version
    private Integer versao;

    @Column(name = "aberta_em")
    private java.time.LocalDateTime abertaEm;

    @Column(name = "esgotada_em")
    private java.time.LocalDateTime esgotadaEm;

    public java.time.LocalDateTime getAbertaEm() { return abertaEm; }
    public void setAbertaEm(java.time.LocalDateTime abertaEm) { this.abertaEm = abertaEm; }
    public java.time.LocalDateTime getEsgotadaEm() { return esgotadaEm; }
    public void setEsgotadaEm(java.time.LocalDateTime esgotadaEm) { this.esgotadaEm = esgotadaEm; }

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