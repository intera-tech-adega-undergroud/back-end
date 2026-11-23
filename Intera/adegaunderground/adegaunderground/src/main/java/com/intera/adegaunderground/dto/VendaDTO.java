package com.intera.adegaunderground.dto;

import com.intera.adegaunderground.entity.DescontoTipo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Venda. Os campos {@code descontoTipo}, {@code descontoValor}, {@code idFuncionarioDesconto}
 * e {@code pagamentos} são opcionais: sem eles a venda segue o formato antigo (uma forma de pagamento,
 * sem desconto).
 */
public record VendaDTO(
        @NotNull @Positive Integer idFuncionario,
        @Positive Integer idCliente,
        @NotBlank String formaPagamento,
        @NotNull Boolean pago,
        @NotEmpty List<@NotNull @Valid ItemVendaDTO> itens,
        DescontoTipo descontoTipo,
        @PositiveOrZero BigDecimal descontoValor,
        @Positive Integer idFuncionarioDesconto,
        List<@NotNull @Valid PagamentoDTO> pagamentos
) {
    /** Construtor do formato antigo (sem desconto e sem pagamento dividido). */
    public VendaDTO(Integer idFuncionario, Integer idCliente, String formaPagamento, Boolean pago,
                    List<ItemVendaDTO> itens) {
        this(idFuncionario, idCliente, formaPagamento, pago, itens, null, null, null, null);
    }
}
