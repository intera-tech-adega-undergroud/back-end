package com.intera.adegaunderground.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
@Table(name = "tbl_produto")
@Schema(description = "Representa um produto da adega (bebidas, itens ou combos)")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do produto", example = "1")
    private Integer idProduto;

    @Schema(description = "Nome do produto", example = "Cerveja Heineken")
    private String nome;

    @Schema(description = "Preço do produto", example = "12.50")
    private Double preco;

    @Schema(description = "Indica se o produto está ativo", example = "true")
    private Boolean ativo;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Tipo de embalagem do produto", example = "LATA")
    private Embalagem embalagem;

    @Schema(description = "Volume do produto em ml (para bebidas)", example = "350")
    private Integer volumeMl;

    @Schema(description = "Quantidade mínima em estoque para alerta", example = "10")
    private Integer qtdMinima;

    @Schema(description = "Quantidade atual em unidades no estoque", example = "50")
    private Integer qtdUnidade;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    @Schema(description = "Categoria do produto")
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
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
}