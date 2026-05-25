package com.intera.adegaunderground.dto;

public class RankFuncionarioDTO {

    private Long idFuncionario;
    private String vendedor;
    private Long quantidadeVendas;
    private Double valorTotalVendido;

    public RankFuncionarioDTO(
            Long idFuncionario,
            String vendedor,
            Long quantidadeVendas,
            Double valorTotalVendido
    ) {
        this.idFuncionario = idFuncionario;
        this.vendedor = vendedor;
        this.quantidadeVendas = quantidadeVendas;
        this.valorTotalVendido = valorTotalVendido;
    }

    public Long getIdFuncionario() {
        return idFuncionario;
    }

    public String getVendedor() {
        return vendedor;
    }

    public Long getQuantidadeVendas() {
        return quantidadeVendas;
    }

    public Double getValorTotalVendido() {
        return valorTotalVendido;
    }
}
