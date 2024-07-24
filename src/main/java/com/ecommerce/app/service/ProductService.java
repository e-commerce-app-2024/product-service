package com.ecommerce.app.service;

import com.ecommerce.app.dto.ProductPurchaseRequest;
import com.ecommerce.app.dto.ProductPurchaseResponse;
import com.ecommerce.app.dto.ProductRequest;
import com.ecommerce.app.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse getProductById(Long id);

    void deleteProduct(Long id);

    List<ProductResponse> getProductsByCategory(Long id);

    List<ProductResponse> getAllProducts();

    ProductResponse addProduct(ProductRequest productRequest);

    ProductResponse updateProduct(Long id, ProductRequest productRequest);

    List<ProductPurchaseResponse> purchaseProduct(List<ProductPurchaseRequest> request);
}
