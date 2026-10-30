package com.intera.adegaunderground.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemVendaDTO(
        @NotNull @Positive Integer idProduto,
        @NotNull @Positive Integer quantidade,
        @Positive Integer volumeMl
) {
}
