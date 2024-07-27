package com.ecommerce.app.dto;

import jakarta.validation.Valid;

import java.util.List;


public record CreatePurchaseRequest(
        @Valid
        List<ProductPurchaseRequest> purchaseList
) {
}
