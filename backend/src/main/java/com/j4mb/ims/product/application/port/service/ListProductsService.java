package com.j4mb.ims.product.application.port.service;

import com.j4mb.ims.product.application.port.in.ListProductsUseCase;
import com.j4mb.ims.product.application.port.out.LoadProductsPort;

import java.util.Objects;

public class ListProductsService implements ListProductsUseCase {
    private static final int MAX_PAGE_SIZE = 100;

    private final LoadProductsPort loadProductsPort;

    public ListProductsService(LoadProductsPort loadProductsPort) {
        this.loadProductsPort = Objects.requireNonNull(loadProductsPort);
    }
        @Override
        public ProductPage listProducts(ListProductsQuery query) {
            Objects.requireNonNull(query, "Query cannot be null");

            if (query.page() < 0) {
                throw new IllegalArgumentException(
                        "Page index cannot be negative"
                );
            }

            if (query.size() < 1 || query.size() > MAX_PAGE_SIZE) {
                throw new IllegalArgumentException(
                        "Page size must be between 1 and " + MAX_PAGE_SIZE
                );
            }

            String search = query.search() == null
                    ? ""
                    : query.search().trim();

            LoadProductsPort.ProductQueryData portQuery = new LoadProductsPort.ProductQueryData(
                    query.page(),
                    query.size(),
                    search,
                    query.activeOnly(),
                    query.lowStockOnly()
            );

            LoadProductsPort.ProductPageData result = loadProductsPort.loadProducts(portQuery);

            return new ProductPage(
                    result.products(),
                    result.page(),
                    result.size(),
                    result.totalElements(),
                    result.totalPages()
            );
        }
    }

