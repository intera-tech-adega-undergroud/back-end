package com.intera.adegaunderground.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum CategoriaBebida {

    BEBIDA_UNIDADE("BEBIDA UNIDADE"),
    BEBIDA_FRACIONADA("BEBIDA FRACIONADA"),
    DIVERSOS("DIVERSOS"),
    DRINK("DRINK"),
    COMBO("COMBO");

    private final String descricao;

    CategoriaBebida(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static CategoriaBebida fromDescricao(String descricao) {
        for (CategoriaBebida categoria : values()) {
            if (categoria.descricao.equalsIgnoreCase(descricao)) {
                return categoria;
            }
        }

        throw new IllegalArgumentException("Categoria inválida: " + descricao);
    }

    @Converter(autoApply = true)
    public static class CategoriaBebidaConverter
            implements AttributeConverter<CategoriaBebida, String> {

        @Override
        public String convertToDatabaseColumn(CategoriaBebida categoria) {
            return categoria != null ? categoria.getDescricao() : null;
        }

        @Override
        public CategoriaBebida convertToEntityAttribute(String descricao) {
            return descricao != null
                    ? CategoriaBebida.fromDescricao(descricao)
                    : null;
        }
    }
}