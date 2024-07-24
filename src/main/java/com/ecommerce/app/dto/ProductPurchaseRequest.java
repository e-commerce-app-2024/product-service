package com.ecommerce.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;


@Validated
public record ProductPurchaseRequest(
        @NotNull(message = "product is required")
        Long productId,
        @NotNull(message = "quantity is required")
        @Positive(message = "quantity is invalid")
        Long quantity
) {
}
