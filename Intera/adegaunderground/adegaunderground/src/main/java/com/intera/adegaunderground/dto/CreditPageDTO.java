package com.intera.adegaunderground.dto;

import java.math.BigDecimal;
import java.util.List;

public record CreditPageDTO(
        List<FiadoDTO> content,
        String nextCursor,
        boolean hasNext,
        int limit,
        BigDecimal totalAberto
) {

    public CreditPageDTO {
        content = List.copyOf(content);
    }
}