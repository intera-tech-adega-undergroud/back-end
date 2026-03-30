package com.intera.adegaunderground.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "tbl_cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCliente;
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    private String email;
    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;
    private Double saldoDevedor;
    @NotBlank(message = "O campo compra fiado é obrigatório")
    private Boolean compraFiado;

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Double getSaldoDevedor() {
        return saldoDevedor;
    }

    public void setSaldoDevedor(Double saldoDevedor) {
        this.saldoDevedor = saldoDevedor;
    }

    public Boolean getCompraFiado() {
        return compraFiado;
    }

    public void setCompraFiado(Boolean compraFiado) {
        this.compraFiado = compraFiado;
    }
}
