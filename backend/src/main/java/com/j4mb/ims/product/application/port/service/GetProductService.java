package com.j4mb.ims.product.application.port.service;

import com.j4mb.ims.product.application.exception.ProductNotFoundException;
import com.j4mb.ims.product.application.port.in.GetProductUseCase;
import com.j4mb.ims.product.application.port.out.LoadProductPort;
import com.j4mb.ims.product.domain.Product;

import java.util.Objects;

public class GetProductService implements GetProductUseCase {

    private final LoadProductPort loadProductPort;

    public GetProductService(LoadProductPort loadProductPort) {
        this.loadProductPort = Objects.requireNonNull(loadProductPort);
    }

    @Override
    public Product getProduct(Long productId) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException(
                    "Product ID must be a positive number"
            );
        }

        return loadProductPort.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}