package com.ecommerce.app.dto;

import java.math.BigDecimal;


public record ProductInfoResponse(
        Long id,
        String name,
        Long quantity,
        BigDecimal price
) {
}
