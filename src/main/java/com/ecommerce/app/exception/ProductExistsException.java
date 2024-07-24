package com.ecommerce.app.exception;

public class ProductExistsException extends RuntimeException {

    public ProductExistsException(String name) {
        super(String.format("product exists with name: %s", name));
    }
}
