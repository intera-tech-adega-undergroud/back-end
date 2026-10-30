package com.intera.adegaunderground.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record VendaDTO(
        @NotNull @Positive Integer idFuncionario,
        @Positive Integer idCliente,
        @NotBlank String formaPagamento,
        @NotNull Boolean pago,
        @NotEmpty List<@NotNull @Valid ItemVendaDTO> itens
) {
}
