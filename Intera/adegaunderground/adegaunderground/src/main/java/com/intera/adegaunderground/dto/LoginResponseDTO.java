package com.intera.adegaunderground.dto;

public record LoginResponseDTO(
        String token,
        Integer id,
        String nome,
        String cargo
) {
}
