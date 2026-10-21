package com.intera.adegaunderground.dto;

import java.util.List;

public record FuncionarioResponseDTO(
        Integer id,
        String nome,
        String email,
        List<String> permissoes
) {
}
