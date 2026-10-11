package com.j4mb.ims.product.application.port.out;

import com.j4mb.ims.product.domain.Product;

import java.util.Optional;

public interface LoadProductPort {
    Optional<Product> findById(Long productId);
}
