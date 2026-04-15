package com.intera.adegaunderground.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "item_evento")
@IdClass(ItemEventoPK.class)
@Schema(description = "Relaciona produtos a um evento (ex: itens de uma venda)")
public class ItemEvento {

    @Id
    @NotNull(message = "Evento é obrigatório")
    @ManyToOne
    @JoinColumn(name = "id_evento", nullable = false)
    @Schema(description = "Evento associado")
    private Evento evento;

    @Id
    @NotNull(message = "Produto é obrigatório")
    @ManyToOne
    @JoinColumn(name = "id_produto", nullable = false)
    @Schema(description = "Produto associado")
    private Produto produto;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade mínima é 1")
    @Column(nullable = false)
    @Schema(description = "Quantidade do produto no evento", example = "2")
    private Integer quantidade;

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}