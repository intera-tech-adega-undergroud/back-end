package com.intera.adegaunderground.dto;

public class GraficoDashDTO {

    private Integer dia;
    private Double valorTotalVendas;

    public GraficoDashDTO(
            Integer dia,
            Double valorTotalVendas
    ) {
        this.dia = dia;
        this.valorTotalVendas = valorTotalVendas;
    }

    public Integer getDia() {
        return dia;
    }

    public Double getValorTotalVendas() {
        return valorTotalVendas;
    }
}
