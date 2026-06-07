package com.intera.adegaunderground.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class EntradaEstoqueDTO {

    @NotNull
    @Min(1)
    private Integer quantidade;

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}