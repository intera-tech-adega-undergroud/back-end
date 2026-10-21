package com.intera.adegaunderground.dto;

import java.util.List;

public record LoginResponseDTO(
        String token,
        Integer id,
        String nome,
        String cargo,
        List<String> permissoes
) {
}
