package com.intera.adegaunderground.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_evento")
@Schema(description = "Representa um evento do sistema (venda, pagamento, movimentação, etc)")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do evento", example = "1")
    private Integer idEvento;

    @NotNull(message = "Tipo do evento é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Tipo do evento", example = "VENDA")
    private TipoEvento tipo;

    @NotNull(message = "A data e hora são obrigatórios")
    @Column(nullable = false)
    @Schema(description = "Data e hora do evento", example = "2026-04-13T22:30:00")
    private LocalDateTime dataHoraEvento = LocalDateTime.now();

    @Schema(description = "Descrição do evento", example = "Venda de combo whisky + energético")
    private String descricao;

    @NotBlank(message = "Forma de pagamento é obrigatória")
    @Schema(description = "Forma de pagamento", example = "PIX")
    private String formaPagamento;

    @NotNull(message = "Campo pago é obrigatório")
    @Schema(description = "Indica se o evento foi pago", example = "true")
    private Boolean pago;

    @ManyToOne
    @JoinColumn(name = "tbl_cliente_id_cliente")
    @Schema(description = "Cliente relacionado ao evento")
    private Cliente cliente;

    @NotNull(message = "Funcionário é obrigatório")
    @ManyToOne
    @JoinColumn(name = "tbl_funcionario_id_funcionario", nullable = false)
    @Schema(description = "Funcionário responsável pelo evento")
    private Funcionario funcionario;

    public Integer getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Integer idEvento) {
        this.idEvento = idEvento;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataHoraEvento() {
        return dataHoraEvento;
    }

    public void setDataHoraEvento(LocalDateTime dataHoraEvento) {
        this.dataHoraEvento = dataHoraEvento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public Boolean getPago() {
        return pago;
    }

    public void setPago(Boolean pago) {
        this.pago = pago;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
}