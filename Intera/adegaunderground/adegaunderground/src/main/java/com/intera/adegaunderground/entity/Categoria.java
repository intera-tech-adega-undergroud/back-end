package com.intera.adegaunderground.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dmsao_categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCategoria;

    @Enumerated(EnumType.STRING)
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
