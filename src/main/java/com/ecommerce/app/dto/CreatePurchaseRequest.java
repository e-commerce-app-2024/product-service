package com.ecommerce.app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public record CreatePurchaseRequest(
        @Valid
        List<ProductPurchaseRequest> purchaseList,
        @NotNull(message = "userName is required")
        String userName
) {
}
