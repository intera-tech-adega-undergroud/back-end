package com.intera.adegaunderground.dto;

import com.intera.adegaunderground.entity.Produto;

import java.math.BigDecimal;

public record StockProductDTO(
        Integer idProduto,
        String nome,
        BigDecimal preco,
        Boolean ativo,
        String embalagem,
        Integer volumeMl,
        Integer qtdMinimo,
        Integer qtdUnidade,
        Integer valorCompra,
        CategoryDTO categoria
) {

    public record CategoryDTO(
            Integer idCategoria,
            String categoria
    ) {
    }

    public static StockProductDTO from(Produto produto) {
        var categoria = produto.getCategoria();

        return new StockProductDTO(
                produto.getIdProduto(),
                produto.getNome(),
                produto.getPreco(),
                produto.getAtivo(),
                produto.getEmbalagem() == null
                        ? null
                        : produto.getEmbalagem().name(),
                produto.getVolumeMl(),
                produto.getQtdMinimo(),
                produto.getQtdUnidade(),
                produto.getValorCompra(),
                categoria == null
                        ? null
                        : new CategoryDTO(
                        categoria.getIdCategoria(),
                        categoria.getCategoria() == null
                                ? null
                                : categoria.getCategoria().name()
                )
        );
    }
}