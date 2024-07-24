package com.ecommerce.app.dto;

import java.math.BigDecimal;


public record ProductResponse(
        Long id,
        String name,
        String description,
        Long quantity,
        BigDecimal price,
        Category category
) {
}
