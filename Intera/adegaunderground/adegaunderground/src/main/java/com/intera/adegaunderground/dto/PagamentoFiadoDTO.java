package com.intera.adegaunderground.dto;

public class PagamentoFiadoDTO {

    private Integer idCliente;
    private Double valorPagamento;
    private String formaPagamento;

    public Integer getIdCliente() {
        return idCliente;
    }

    public Double getValorPagamento() {
        return valorPagamento;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }
}