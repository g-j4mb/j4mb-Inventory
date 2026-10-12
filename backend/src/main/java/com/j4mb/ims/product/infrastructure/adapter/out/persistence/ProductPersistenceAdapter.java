package com.j4mb.ims.product.infrastructure.adapter.out.persistence;

import com.j4mb.ims.product.application.port.out.LoadProductPort;
import com.j4mb.ims.product.application.port.out.LoadProductsPort;
import com.j4mb.ims.product.application.port.out.SaveProductPort;
import com.j4mb.ims.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class ProductPersistenceAdapter implements LoadProductPort, SaveProductPort, LoadProductsPort {
    private final ProductRepository repository;
    public ProductPersistenceAdapter(
            ProductRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long productId) {
        return repository.findById(productId)
                .map(ProductJpaEntity::toDomain);
    }

    @Override
    @Transactional
    public Product save(Product product) {
        ProductJpaEntity entity;

        if (product.getId() == null) {
            entity = ProductJpaEntity.fromDomain(product);
        } else {
            entity = repository.findById(product.getId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Cannot save product because it no longer exists: "
                                    + product.getId()
                    ));

            entity.copyFrom(product);
        }

        ProductJpaEntity savedEntity = repository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductPageData loadProducts(ProductQueryData query) {
        Pageable pageable = PageRequest.of(
                query.page(),
                query.size(),
                Sort.by(Sort.Direction.ASC, "id")
        );
        String search = query.search()==null?"":query.search();
        Page<ProductJpaEntity> result = repository.searchProducts(
                search,
                query.activeOnly(),
                query.lowStockOnly(),
                pageable
        );
        return new ProductPageData(
                result.getContent().stream()
                        .map(ProductJpaEntity::toDomain)
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}
