package com.ecommerce.app.controller;


import com.ecommerce.app.dto.*;
import com.ecommerce.app.payload.AppResponse;
import com.ecommerce.app.payload.PageResponse;
import com.ecommerce.app.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public AppResponse<ProductResponse> addProduct(@Valid @RequestBody ProductRequest request) {
        return AppResponse.created(productService.addProduct(request));
    }

    @PostMapping("/info")
    public AppResponse<List<ProductInfoResponse>> getProductsInfo(@RequestBody List<Long> ids) {
        return AppResponse.created(productService.getProductsInfo(ids));
    }

    @PostMapping("/purchase")
    public AppResponse<PurchaseResponse> purchaseProduct(@Valid @RequestBody CreatePurchaseRequest request) {
        PurchaseResponse purchaseResponse = productService.purchaseProduct(request);
        return AppResponse.created(purchaseResponse, purchaseResponse.requestId());
    }

    @PutMapping("/{id}")
    public AppResponse<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest ProductRequest) {
        return AppResponse.ok(productService.updateProduct(id, ProductRequest));
    }

    @GetMapping("/{id}")
    public AppResponse<ProductResponse> getProductById(@PathVariable Long id) {
        return AppResponse.ok(productService.getProductById(id));
    }

    @GetMapping("/random/{size}")
    public AppResponse<List<ProductResponse>> getRandomProducts(@PathVariable(name = "size") @Positive(message = "add valid size to get the random list") int size) {
        return AppResponse.ok(productService.getRandomProducts(size));
    }

    @GetMapping
    public AppResponse<PageResponse<ProductViewResponse>> getAllProducts(@Valid @RequestBody ProductFilterRequest filterRequest) {
        return AppResponse.ok(productService.getAllProducts(filterRequest, false));
    }

    @DeleteMapping("/{id}")
    public AppResponse<ProductResponse> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return AppResponse.noContent();
    }
}
