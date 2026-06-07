package com.intera.adegaunderground.dto;

public class CompraFiadoDTO {

    private String descricao;
    private Double valor;

    public CompraFiadoDTO(String descricao, Double valor) {
        this.descricao = descricao;
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public Double getValor() {
        return valor;
    }
}