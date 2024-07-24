package com.ecommerce.app.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long id) {
        super(String.format("Category not found with id: %s", id));
    }
}
