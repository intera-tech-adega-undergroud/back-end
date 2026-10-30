package com.intera.adegaunderground.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PagamentoFiadoDTO(
        @NotNull @Positive Integer idCliente,
        @NotNull @Positive @Digits(integer = 8, fraction = 2) BigDecimal valorPagamento,
        @NotBlank String formaPagamento
) {
}
