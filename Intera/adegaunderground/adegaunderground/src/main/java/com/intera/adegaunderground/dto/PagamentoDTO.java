package com.intera.adegaunderground.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/** Uma parcela do pagamento da venda. {@code forma} é o código (DINHEIRO, PIX, DEBITO, CREDITO, FIADO). */
public record PagamentoDTO(
        @NotBlank String forma,
        @NotNull @Positive BigDecimal valorRecebido
) {
}
