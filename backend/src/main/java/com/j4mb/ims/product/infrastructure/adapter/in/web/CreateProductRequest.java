package com.j4mb.ims.product.infrastructure.adapter.in.web;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank
        @Size(max = 64)
        String sku,

        @NotBlank
        @Size(max = 150)
        String name,

        String description,

        @NotBlank
        @Size(max = 20)
        String unitOfMeasure,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal unitCost,

        @PositiveOrZero
        int quantityOnHand,

        @PositiveOrZero
        int reorderLevel
) {
}