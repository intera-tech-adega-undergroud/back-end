package com.intera.adegaunderground.dto;

import java.util.List;

public class FiadoDTO {

    private Integer idCliente;
    private String cliente;
    private Double valor;
    private String data;
    private String status;
    private List<CompraFiadoDTO> compras;

    public FiadoDTO(Integer idCliente, String cliente, Double valor, String data, String status, List<CompraFiadoDTO> compras) {
        this.idCliente = idCliente;
        this.cliente = cliente;
        this.valor = valor;
        this.data = data;
        this.status = status;
        this.compras = compras;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public String getCliente() {
        return cliente;
    }

    public Double getValor() {
        return valor;
    }

    public String getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    public List<CompraFiadoDTO> getCompras() {
        return compras;
    }
}