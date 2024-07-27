package com.ecommerce.app.controller;


import com.ecommerce.app.dto.*;
import com.ecommerce.app.payload.AppResponse;
import com.ecommerce.app.payload.PageResponse;
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
    public AppResponse<List<ProductPurchaseResponse>> purchaseProduct(@Valid @RequestBody CreatePurchaseRequest request) {
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
    public AppResponse<PageResponse<ProductResponse>> getAllProducts(@Valid @RequestBody ProductFilterRequest filterRequest) {
        return AppResponse.ok(productService.getAllProducts(filterRequest));
    }

    @DeleteMapping("/{id}")
    public AppResponse<ProductResponse> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return AppResponse.noContent();
    }
}
