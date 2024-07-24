package com.ecommerce.app.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductPurchaseResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        BigDecimal totalPrice
) {
}
