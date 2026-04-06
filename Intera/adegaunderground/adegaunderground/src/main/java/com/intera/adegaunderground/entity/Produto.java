package com.intera.adegaunderground.entity;

import jakarta.persistence.*;

// Produto.java
@Entity
@Table(name = "tbl_produto")
public class Produto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProduto;
    private String nome;
    private Double preco;
    private Boolean ativo;
    @Enumerated(EnumType.STRING)
    private Embalagem embalagem;
    private Integer volumeMl;
    private Integer qtdMinima;
    private Integer qtdUnidade;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    public Integer getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Integer idProduto) {
        this.idProduto = idProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Embalagem getEmbalagem() {
        return embalagem;
    }

    public void setEmbalagem(Embalagem embalagem) {
        this.embalagem = embalagem;
    }

    public Integer getVolumeMl() {
        return volumeMl;
    }

    public void setVolumeMl(Integer volumeMl) {
        this.volumeMl = volumeMl;
    }

    public Categoria getCategoria() { return categoria; }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Integer getQtdMinima() { return qtdMinima; }

    public void setQtdMinima(Integer qtdMinima) { this.qtdMinima = qtdMinima; }

    public Integer getQtdUnidade() { return qtdUnidade; }

    public void setQtdUnidade(Integer qtdUnidade) { this.qtdUnidade = qtdUnidade; }
}

