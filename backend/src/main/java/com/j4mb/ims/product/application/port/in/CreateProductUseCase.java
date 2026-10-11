package com.j4mb.ims.product.application.port.in;

import com.j4mb.ims.product.domain.Product;

import java.math.BigDecimal;

public interface CreateProductUseCase {
    Product createProduct(CreateProductCommand command);
    record CreateProductCommand(
            String sku,
            String name,
            String description,
            String unitOfMeasure,
            BigDecimal unitCost,
            int quantityOnHand,
            int reorderLevel
    ) {}
}
