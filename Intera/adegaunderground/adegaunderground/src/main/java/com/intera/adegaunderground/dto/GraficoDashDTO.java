package com.intera.adegaunderground.dto;

import java.time.LocalDate;

public class GraficoDashDTO {

    private LocalDate data;
    private Double valorTotalVendas;

    public GraficoDashDTO(
            LocalDate data,
            Double valorTotalVendas
    ) {
        this.data = data;
        this.valorTotalVendas = valorTotalVendas;
    }

    public LocalDate getData() {
        return data;
    }

    public Double getValorTotalVendas() {
        return valorTotalVendas;
    }
}
