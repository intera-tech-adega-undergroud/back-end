package com.intera.adegaunderground.dto;

import com.intera.adegaunderground.entity.CategoriaBebida;
import com.intera.adegaunderground.entity.Embalagem;

import java.math.BigDecimal;

public class ProdutoRequestDTO {

    private String nome;
    private BigDecimal preco;

    private Embalagem embalagem;

    private Integer volumeMl;
    private Integer qtdMinima;
    private Integer qtdUnidade;


    private CategoriaBebida categoria;

    public CategoriaBebida getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaBebida categoria) {
        this.categoria = categoria;
    }

    public Embalagem getEmbalagem() {
        return embalagem;
    }

    public void setEmbalagem(Embalagem embalagem) {
        this.embalagem = embalagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getQtdMinima() {
        return qtdMinima;
    }

    public void setQtdMinima(Integer qtdMinima) {
        this.qtdMinima = qtdMinima;
    }

    public Integer getQtdUnidade() {
        return qtdUnidade;
    }

    public void setQtdUnidade(Integer qtdUnidade) {
        this.qtdUnidade = qtdUnidade;
    }

    public Integer getVolumeMl() {
        return volumeMl;
    }

    public void setVolumeMl(Integer volumeMl) {
        this.volumeMl = volumeMl;
    }
}