package com.ecommerce.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;


public record ProductRequest(
        Long id,
        @NotNull(message = "product name shouldn't be null or empty")
        @Size(min = 5, max = 30, message = "product name length shouldn be between 5 and 30")
        String name,
        @Size(min = 5, max = 30, message = "product description length shouldn be between 5 and 30")
        String description,
        @NotNull(message = "quantity is required")
        @Positive(message = "quantity is invalid")
        Long quantity,
        @NotNull(message = "product price shouldn't be null or empty")
        @Positive(message = "price is invalid")
        BigDecimal price,
        @NotNull(message = "product category is required")
        Long categoryId
) {
}
