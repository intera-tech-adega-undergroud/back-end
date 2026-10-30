package com.intera.adegaunderground.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tbl_cliente")
@Schema(description = "Representa um cliente da adega")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do cliente", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
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
    @DecimalMin(value = "0", message = "O valor mínimo é 0")
    @Digits(integer = 8, fraction = 2)
    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Saldo devedor do cliente", example = "0.00")
    private BigDecimal saldoDevedor = BigDecimal.ZERO;

    @NotNull(message = "O campo compra fiado é obrigatório")
    @Column(nullable = false)
    @Schema(description = "Indica se o cliente pode comprar fiado", example = "true")
    private Boolean compraFiado = false;

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

    public BigDecimal getSaldoDevedor() {
        return saldoDevedor;
    }

    public void setSaldoDevedor(BigDecimal saldoDevedor) {
        this.saldoDevedor = saldoDevedor;
    }

    public Boolean getCompraFiado() {
        return compraFiado;
    }

    public void setCompraFiado(Boolean compraFiado) {
        this.compraFiado = compraFiado;
    }
}