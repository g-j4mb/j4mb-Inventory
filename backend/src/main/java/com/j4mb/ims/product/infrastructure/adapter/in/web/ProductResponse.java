package com.j4mb.ims.product.infrastructure.adapter.in.web;

import com.j4mb.ims.product.domain.Product;

import java.math.BigDecimal;

public record ProductResponse(Long id,
                              String sku,
                              String name,
                              String description,
                              String unitOfMeasure,
                              BigDecimal unitCost,
                              int quantityOnHand,
                              int reorderLevel,
                              boolean active
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSKU(),
                product.getName(),
                product.getDescription(),
                product.getUnitOfMeasure(),
                product.getUnitCost(),
                product.getQuantityOnHand(),
                product.getReorderLevel(),
                product.isActive()
        );
    }
}