package com.j4mb.ims.product.application.port.in;

import com.j4mb.ims.product.domain.Product;

import java.math.BigDecimal;

public interface UpdateProductUseCase {
    public  Product updateProduct(UpdateProductCommand updateProductCommand);
    record UpdateProductCommand(
            Long productId,
            String name,
            String description,
            String unitOfMeasure,
            BigDecimal unitCost,
            int reorderLevel
    ){}
}
