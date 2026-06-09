package com.intera.adegaunderground.dto;

public class NotaFiscalResponseDTO {

    private String textoExtraido;

    public NotaFiscalResponseDTO(String textoExtraido) {
        this.textoExtraido = textoExtraido;
    }

    public String getTextoExtraido() {
        return textoExtraido;
    }

    public void setTextoExtraido(String textoExtraido) {
        this.textoExtraido = textoExtraido;
    }
}