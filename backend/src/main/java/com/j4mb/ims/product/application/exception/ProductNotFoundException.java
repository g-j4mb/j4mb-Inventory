package com.j4mb.ims.product.application.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId) {
        super("Product with id " + productId + " not found");
    }
}
