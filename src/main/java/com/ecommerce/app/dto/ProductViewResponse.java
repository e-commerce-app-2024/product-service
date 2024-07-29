package com.ecommerce.app.dto;

import java.math.BigDecimal;


public record ProductViewResponse(
        Long id,
        String name,
        String description,
        Long quantity,
        BigDecimal price,
        Long categoryId,
        String categoryName
) {
}
