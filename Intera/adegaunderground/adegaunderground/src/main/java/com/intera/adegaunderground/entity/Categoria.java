package com.intera.adegaunderground.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "dmsao_categoria")
@Schema(description = "Representa a categoria de uma bebida/produto")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID da categoria", example = "1")
    private Integer idCategoria;


    @Enumerated(EnumType.STRING)
    @NotNull(message = "Categoria é obrigatória")
    @Schema(
            description = "Tipo da categoria",
            example = "CERVEJA"
    )
    private CategoriaBebida categoria; // Enum criado anteriormente

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public CategoriaBebida getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaBebida categoria) {
        this.categoria = categoria;
    }
}
