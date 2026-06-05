package com.intera.adegaunderground.dto;

import java.time.LocalDateTime;

public class FiadoDTO {

    private Integer idCliente;
    private String nome;
    private Double saldoDevedor;
    private LocalDateTime dataVenda;
    private Boolean pago;

    public FiadoDTO(
            Integer idCliente,
            String nome,
            Double saldoDevedor,
            LocalDateTime dataVenda,
            Boolean pago
    ) {
        this.idCliente = idCliente;
        this.nome = nome;
        this.saldoDevedor = saldoDevedor;
        this.dataVenda = dataVenda;
        this.pago = pago;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public String getNome() {
        return nome;
    }

    public Double getSaldoDevedor() {
        return saldoDevedor;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public Boolean getPago() {
        return pago;
    }
}