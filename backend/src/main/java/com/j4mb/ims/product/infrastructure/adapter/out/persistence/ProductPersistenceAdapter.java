package com.j4mb.ims.product.infrastructure.adapter.out.persistence;

import com.j4mb.ims.product.application.port.out.LoadProductPort;
import com.j4mb.ims.product.application.port.out.SaveProductPort;
import com.j4mb.ims.product.domain.Product;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class ProductPersistenceAdapter implements LoadProductPort, SaveProductPort {
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
}
