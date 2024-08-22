package com.ecommerce.app.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PurchaseResponse(
        List<ProductPurchaseResponse> products,
        String requestId,
        String token,
        String userName
) {
}
