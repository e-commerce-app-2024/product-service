package com.ecommerce.app.exception;

public class CategoryExistsException extends RuntimeException {

    public CategoryExistsException(String name) {
        super(String.format("Category exists with name: %s", name));
    }
}
