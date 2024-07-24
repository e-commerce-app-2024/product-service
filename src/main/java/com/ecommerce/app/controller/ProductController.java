package com.ecommerce.app.controller;


import com.ecommerce.app.payload.AppResponse;
import com.ecommerce.app.dto.ProductPurchaseRequest;
import com.ecommerce.app.dto.ProductPurchaseResponse;
import com.ecommerce.app.dto.ProductRequest;
import com.ecommerce.app.dto.ProductResponse;
import com.ecommerce.app.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public AppResponse<ProductResponse> addProduct(@Valid @RequestBody ProductRequest request) {
        return AppResponse.created(productService.addProduct(request));
    }

    @PostMapping("/purchase")
    public AppResponse<List<ProductPurchaseResponse>> purchaseProduct(@Valid @RequestBody List<ProductPurchaseRequest> request) {
        return AppResponse.created(productService.purchaseProduct(request));
    }

    @PutMapping("/{id}")
    public AppResponse<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest ProductRequest) {
        return AppResponse.ok(productService.updateProduct(id, ProductRequest));
    }

    @GetMapping("/{id}")
    public AppResponse<ProductResponse> getProductById(@PathVariable Long id) {
        return AppResponse.ok(productService.getProductById(id));
    }

    @GetMapping
    public AppResponse<List<ProductResponse>> getAllProducts() {
        return AppResponse.ok(productService.getAllProducts());
    }

    @GetMapping("/category/{id}")
    public AppResponse<List<ProductResponse>> getProductsByCategory(@PathVariable Long id) {
        return AppResponse.ok(productService.getProductsByCategory(id));
    }

    @DeleteMapping("/{id}")
    public AppResponse<ProductResponse> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return AppResponse.noContent();
    }
}
