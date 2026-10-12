package com.j4mb.ims.product.application.port.out;

import com.j4mb.ims.product.domain.Product;

import java.util.List;

public interface LoadProductsPort {
    ProductPageData loadProducts(ProductQueryData query);

    record ProductQueryData(
            int page,
            int size,
            String search,
            Boolean activeOnly,
            Boolean lowStockOnly
    ) {}

    record ProductPageData(
            List<Product> products,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}
}
