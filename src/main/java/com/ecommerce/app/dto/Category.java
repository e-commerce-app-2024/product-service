package com.ecommerce.app.dto;

import lombok.Builder;

@Builder
public record Category(
        Long id,
        String name,
        String description
) {
}
