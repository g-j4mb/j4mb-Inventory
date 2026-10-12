package com.j4mb.ims.product.application.port.in;

import com.j4mb.ims.product.domain.Product;

import java.util.List;

public interface ListProductsUseCase {

    ProductPage listProducts(ListProductsQuery query);

    record ListProductsQuery(
            int page,
            int size,
            String search,
            Boolean activeOnly,
            Boolean lowStockOnly
    ) {}

    record ProductPage(
            List<Product> products,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}
}
