package com.intera.adegaunderground.dto;

public class VendaBigNumberDTO {

    private Double valorTotalVendido;
    private Long totalVendas;

    public VendaBigNumberDTO(Double valorTotalVendido, Long totalVendas) {
        this.valorTotalVendido = valorTotalVendido;
        this.totalVendas = totalVendas;
    }

    public Double getValorTotalVendido() {
        return valorTotalVendido;
    }

    public Long getTotalVendas() {
        return totalVendas;
    }
}
