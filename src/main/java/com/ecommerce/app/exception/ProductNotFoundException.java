package com.ecommerce.app.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super(String.format("Product not found with id: %s", id));
    }
}
