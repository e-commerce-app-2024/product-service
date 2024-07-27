package com.ecommerce.app.service;

import com.ecommerce.app.dto.*;
import com.ecommerce.app.payload.PageResponse;

import java.util.List;

public interface ProductService {

    ProductResponse getProductById(Long id);

    void deleteProduct(Long id);

    PageResponse<ProductResponse> getAllProducts(ProductFilterRequest request);

    ProductResponse addProduct(ProductRequest productRequest);

    ProductResponse updateProduct(Long id, ProductRequest productRequest);

    List<ProductPurchaseResponse> purchaseProduct(CreatePurchaseRequest request);
}
