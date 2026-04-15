package com.intera.adegaunderground.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "tbl_cliente")
@Schema(description = "Representa um cliente da adega")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do cliente", example = "1")
    private Integer idCliente;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    @Schema(description = "Nome do cliente", example = "João Silva")
    private String nome;

    @Email(message = "Email inválido")
    @Schema(description = "Email do cliente", example = "joao@email.com")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Column(nullable = false)
    @Schema(description = "Telefone do cliente", example = "11999999999")
    private String telefone;

    @NotNull(message = "Saldo devedor é obrigatório")
    @Min(value = 0, message = "O valor mínimo é 0")
    @Column(nullable = false)
    @Schema(description = "Saldo devedor do cliente", example = "0.0")
    private Double saldoDevedor;

    @NotNull(message = "O campo compra fiado é obrigatório")
    @Column(nullable = false)
    @Schema(description = "Indica se o cliente pode comprar fiado", example = "true")
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