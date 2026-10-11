package com.j4mb.ims.product.application.port.service;

import com.j4mb.ims.product.application.exception.ProductNotFoundException;
import com.j4mb.ims.product.application.port.in.DeleteProductUseCase;
import com.j4mb.ims.product.application.port.out.LoadProductPort;
import com.j4mb.ims.product.application.port.out.SaveProductPort;
import com.j4mb.ims.product.domain.Product;

import java.util.Objects;

public class DeleteProductService implements DeleteProductUseCase {

    private final LoadProductPort loadProductPort;
    private final SaveProductPort saveProductPort;

    public DeleteProductService(
            LoadProductPort loadProductPort,
            SaveProductPort saveProductPort
    ) {
        this.loadProductPort = Objects.requireNonNull(loadProductPort);
        this.saveProductPort = Objects.requireNonNull(saveProductPort);
    }

    @Override
    public void deleteProduct(Long productId) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException(
                    "Product ID must be a positive number"
            );
        }

        Product product = loadProductPort.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.deactivate();
        saveProductPort.save(product);
    }
}
