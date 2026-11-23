package com.intera.adegaunderground.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_funcionario_executor")
    private Funcionario executor;

    @Column(precision = 10, scale = 2)
    @Schema(description = "Soma dos itens antes do desconto")
    private BigDecimal subtotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "desconto_tipo")
    private DescontoTipo descontoTipo;

    @Column(name = "desconto_percentual", precision = 5, scale = 2)
    private BigDecimal descontoPercentual;

    @Column(name = "desconto_valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal descontoValor = BigDecimal.ZERO;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_funcionario_desconto")
    private Funcionario funcionarioDesconto;

    @Column(precision = 10, scale = 2)
    @Schema(description = "Valor final da venda (subtotal - desconto)")
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVenda status = StatusVenda.CONCLUIDA;

    @Column(name = "cancelado_em")
    private LocalDateTime canceladoEm;

    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;

    @Column(name = "valores_estimados", nullable = false)
    @Schema(description = "true quando os valores foram reconstruídos de uma venda antiga")
    private Boolean valoresEstimados = false;

    public Funcionario getExecutor() { return executor; }
    public void setExecutor(Funcionario executor) { this.executor = executor; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public DescontoTipo getDescontoTipo() { return descontoTipo; }
    public void setDescontoTipo(DescontoTipo descontoTipo) { this.descontoTipo = descontoTipo; }
    public BigDecimal getDescontoPercentual() { return descontoPercentual; }
    public void setDescontoPercentual(BigDecimal descontoPercentual) { this.descontoPercentual = descontoPercentual; }
    public BigDecimal getDescontoValor() { return descontoValor; }
    public void setDescontoValor(BigDecimal descontoValor) { this.descontoValor = descontoValor; }
    public Funcionario getFuncionarioDesconto() { return funcionarioDesconto; }
    public void setFuncionarioDesconto(Funcionario funcionarioDesconto) { this.funcionarioDesconto = funcionarioDesconto; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public StatusVenda getStatus() { return status; }
    public void setStatus(StatusVenda status) { this.status = status; }
    public LocalDateTime getCanceladoEm() { return canceladoEm; }
    public void setCanceladoEm(LocalDateTime canceladoEm) { this.canceladoEm = canceladoEm; }
    public String getMotivoCancelamento() { return motivoCancelamento; }
    public void setMotivoCancelamento(String motivoCancelamento) { this.motivoCancelamento = motivoCancelamento; }
    public Boolean getValoresEstimados() { return valoresEstimados; }
    public void setValoresEstimados(Boolean valoresEstimados) { this.valoresEstimados = valoresEstimados; }
}
