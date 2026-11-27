package com.intera.adegaunderground.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Livro de movimentações de volume (ml) de cada garrafa. */
@Entity
@Table(name = "movimento_volume")
public class MovimentoVolume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimento_volume")
    private Integer id;

    @Column(name = "id_unidade_bebida", nullable = false)
    private Integer idUnidadeBebida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentoVolume tipo;

    @Column(name = "ml_delta", nullable = false)
    private Integer mlDelta;

    @Column(name = "ml_anterior", nullable = false)
    private Integer mlAnterior;

    @Column(name = "ml_posterior", nullable = false)
    private Integer mlPosterior;

    @Column(name = "id_produto_vendido")
    private Integer idProdutoVendido;

    @Column(name = "id_evento")
    private Integer idEvento;

    @Column(name = "id_funcionario")
    private Integer idFuncionario;

    private String motivo;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    public Integer getId() { return id; }
    public Integer getIdUnidadeBebida() { return idUnidadeBebida; }
    public void setIdUnidadeBebida(Integer idUnidadeBebida) { this.idUnidadeBebida = idUnidadeBebida; }
    public TipoMovimentoVolume getTipo() { return tipo; }
    public void setTipo(TipoMovimentoVolume tipo) { this.tipo = tipo; }
    public Integer getMlDelta() { return mlDelta; }
    public void setMlDelta(Integer mlDelta) { this.mlDelta = mlDelta; }
    public Integer getMlAnterior() { return mlAnterior; }
    public void setMlAnterior(Integer mlAnterior) { this.mlAnterior = mlAnterior; }
    public Integer getMlPosterior() { return mlPosterior; }
    public void setMlPosterior(Integer mlPosterior) { this.mlPosterior = mlPosterior; }
    public Integer getIdProdutoVendido() { return idProdutoVendido; }
    public void setIdProdutoVendido(Integer idProdutoVendido) { this.idProdutoVendido = idProdutoVendido; }
    public Integer getIdEvento() { return idEvento; }
    public void setIdEvento(Integer idEvento) { this.idEvento = idEvento; }
    public Integer getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Integer idFuncionario) { this.idFuncionario = idFuncionario; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
