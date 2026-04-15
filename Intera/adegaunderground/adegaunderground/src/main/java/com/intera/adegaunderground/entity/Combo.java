package com.intera.adegaunderground.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tbl_combo")
@IdClass(ComboPK.class)
@Schema(description = "Representa os itens que compõem um combo de produtos")
public class Combo {

    @Id
    @NotNull(message = "Produto pai é obrigatório")
    @ManyToOne
    @JoinColumn(name = "tbl_produto_id_combo", nullable = false)
    @Schema(description = "Produto principal (combo)", example = "1")
    private Produto produtoPai;

    @Id
    @NotNull(message = "Produto filho é obrigatório")
    @ManyToOne
    @JoinColumn(name = "tbl_produto_id_produto", nullable = false)
    @Schema(description = "Produto que faz parte do combo", example = "2")
    private Produto produtoFilho;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade mínima é 1")
    @Column(name = "quantidade_produto", nullable = false)
    @Schema(description = "Quantidade do item dentro do combo", example = "4")
    private Integer quantidade;

    public Produto getProdutoPai() {
        return produtoPai;
    }

    public void setProdutoPai(Produto produtoPai) {
        this.produtoPai = produtoPai;
    }

    public Produto getProdutoFilho() {
        return produtoFilho;
    }

    public void setProdutoFilho(Produto produtoFilho) {
        this.produtoFilho = produtoFilho;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}