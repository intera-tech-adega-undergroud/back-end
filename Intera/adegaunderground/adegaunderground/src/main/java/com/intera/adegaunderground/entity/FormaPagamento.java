package com.intera.adegaunderground.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dmsao_forma_pagamento")
public class FormaPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_forma_pagamento")
    private Integer idFormaPagamento;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 45)
    private String descricao;

    @Column(name = "eh_dinheiro", nullable = false)
    private Boolean ehDinheiro = false;

    @Column(name = "gera_saldo_devedor", nullable = false)
    private Boolean geraSaldoDevedor = false;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Integer getIdFormaPagamento() { return idFormaPagamento; }
    public void setIdFormaPagamento(Integer idFormaPagamento) { this.idFormaPagamento = idFormaPagamento; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Boolean getEhDinheiro() { return ehDinheiro; }
    public void setEhDinheiro(Boolean ehDinheiro) { this.ehDinheiro = ehDinheiro; }
    public Boolean getGeraSaldoDevedor() { return geraSaldoDevedor; }
    public void setGeraSaldoDevedor(Boolean geraSaldoDevedor) { this.geraSaldoDevedor = geraSaldoDevedor; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
