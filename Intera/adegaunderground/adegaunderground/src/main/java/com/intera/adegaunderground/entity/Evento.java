package com.intera.adegaunderground.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_evento")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEvento;
    @Enumerated(EnumType.STRING)
    private TipoEvento tipo;
    @NotNull(message = "A data e hora são campos obrigatórios")
    private LocalDateTime dataHoraEvento = LocalDateTime.now();
    private String descricao;
    private String formaPagamento;
    private Boolean pago;

    @ManyToOne
    @JoinColumn(name = "tbl_cliente_id_cliente")
    private Cliente cliente;

    @NotBlank(message = "Funcionario é obrigatório")
    @ManyToOne
    @JoinColumn(name = "tbl_funcionario_id_funcionario")
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

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
}
