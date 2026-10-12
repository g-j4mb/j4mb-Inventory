package com.j4mb.ims.product.infrastructure.adapter.in.web;

import com.j4mb.ims.product.application.port.in.ListProductsUseCase;

import java.util.List;

public record ProductPageResponse(
        List<ProductResponse> products,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static ProductPageResponse from(
            ListProductsUseCase.ProductPage result) {

        List<ProductResponse> products = result.products()
                .stream()
                .map(ProductResponse::from)
                .toList();

        return new ProductPageResponse(
                products,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}