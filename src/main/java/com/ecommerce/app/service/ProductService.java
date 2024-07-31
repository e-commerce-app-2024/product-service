package com.ecommerce.app.service;

import com.ecommerce.app.dto.*;
import com.ecommerce.app.payload.PageResponse;

import java.util.List;

public interface ProductService {

    ProductResponse getProductById(Long id);

    List<ProductResponse> getRandomProducts(int size);

    void deleteProduct(Long id);

    PageResponse<ProductResponse> getAllProducts(ProductFilterRequest request);

    PageResponse<ProductViewResponse> getAllProducts(ProductFilterRequest request, boolean refreshView);

    ProductResponse addProduct(ProductRequest productRequest);

    ProductResponse updateProduct(Long id, ProductRequest productRequest);

    PurchaseResponse purchaseProduct(CreatePurchaseRequest request);

    void rollbackPurchase(String requestId);
}
