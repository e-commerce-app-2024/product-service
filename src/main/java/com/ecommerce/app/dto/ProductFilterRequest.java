package com.ecommerce.app.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;

public record ProductFilterRequest(

        @NotNull(message = "Page index is required")
        Long index,
        @NotNull(message = "Page size is required")
        Long size,
        String sortBy,
        Sort.Direction sort,
        Long categoryId
) {
}
