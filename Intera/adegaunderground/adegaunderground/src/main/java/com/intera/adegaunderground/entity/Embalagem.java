package com.intera.adegaunderground.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum Embalagem {

    ND("N/D"),
    GARRAFA("GARRAFA"),
    LATA("LATA");

    private final String descricao;

    Embalagem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static Embalagem fromDescricao(String descricao) {

        for (Embalagem embalagem : values()) {
            if (embalagem.descricao.equalsIgnoreCase(descricao)) {
                return embalagem;
            }
        }

        throw new IllegalArgumentException(
                "Embalagem inválida: " + descricao
        );
    }

    @Converter(autoApply = true)
    public static class EmbalagemConverter
            implements AttributeConverter<Embalagem, String> {

        @Override
        public String convertToDatabaseColumn(Embalagem embalagem) {
            return embalagem != null
                    ? embalagem.getDescricao()
                    : null;
        }

        @Override
        public Embalagem convertToEntityAttribute(String descricao) {
            return descricao != null
                    ? Embalagem.fromDescricao(descricao)
                    : null;
        }
    }
}