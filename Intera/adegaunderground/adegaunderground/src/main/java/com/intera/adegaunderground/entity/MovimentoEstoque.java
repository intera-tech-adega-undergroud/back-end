package com.intera.adegaunderground.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Livro de movimentações de estoque (unidades). O saldo pode ficar negativo. */
@Entity
@Table(name = "movimento_estoque")
public class MovimentoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimento_estoque")
    private Integer id;

    @Column(name = "id_produto", nullable = false)
    private Integer idProduto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentoEstoque tipo;

    @Column(name = "quantidade_delta", nullable = false)
    private Integer quantidadeDelta;

    @Column(name = "saldo_anterior", nullable = false)
    private Integer saldoAnterior;

    @Column(name = "saldo_posterior", nullable = false)
    private Integer saldoPosterior;

    @Column(name = "id_evento")
    private Integer idEvento;

    @Column(name = "id_funcionario")
    private Integer idFuncionario;

    private String motivo;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    public Integer getId() { return id; }
    public Integer getIdProduto() { return idProduto; }
    public void setIdProduto(Integer idProduto) { this.idProduto = idProduto; }
    public TipoMovimentoEstoque getTipo() { return tipo; }
    public void setTipo(TipoMovimentoEstoque tipo) { this.tipo = tipo; }
    public Integer getQuantidadeDelta() { return quantidadeDelta; }
    public void setQuantidadeDelta(Integer quantidadeDelta) { this.quantidadeDelta = quantidadeDelta; }
    public Integer getSaldoAnterior() { return saldoAnterior; }
    public void setSaldoAnterior(Integer saldoAnterior) { this.saldoAnterior = saldoAnterior; }
    public Integer getSaldoPosterior() { return saldoPosterior; }
    public void setSaldoPosterior(Integer saldoPosterior) { this.saldoPosterior = saldoPosterior; }
    public Integer getIdEvento() { return idEvento; }
    public void setIdEvento(Integer idEvento) { this.idEvento = idEvento; }
    public Integer getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Integer idFuncionario) { this.idFuncionario = idFuncionario; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
