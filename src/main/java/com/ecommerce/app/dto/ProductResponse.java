package com.ecommerce.app.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductResponse(
        Long id,
        String name,
        String description,
        Long quantity,
        BigDecimal price,
        Category category
) {
}
