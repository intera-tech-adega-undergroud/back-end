package com.intera.adegaunderground.dto;

import com.intera.adegaunderground.entity.CategoriaBebida;
import com.intera.adegaunderground.entity.Embalagem;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProdutoRequestDTO(
        @NotBlank @Size(max = 255) String nome,
        @NotNull @DecimalMin("0.00") @Digits(integer = 5, fraction = 2) BigDecimal preco,
        @NotNull Embalagem embalagem,
        @NotNull @PositiveOrZero Integer volumeMl,
        @NotNull @PositiveOrZero Integer qtdMinima,
        @NotNull Integer qtdUnidade,
        @NotNull CategoriaBebida categoria
) {
}
