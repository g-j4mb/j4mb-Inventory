package com.j4mb.ims.product.infrastructure.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<ProductJpaEntity, Long> {
    @Query(
            value = """
            SELECT p
            FROM ProductJpaEntity p
            WHERE
                (:search=''
                    OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:activeOnly IS NULL OR p.active = :activeOnly)
                AND (
                    :lowStockOnly IS NULL
                    OR (:lowStockOnly = TRUE
                        AND p.quantityOnHand <= p.reorderLevel)
                    OR (:lowStockOnly = FALSE
                        AND p.quantityOnHand > p.reorderLevel)
                )
            """,
            countQuery = """
            SELECT COUNT(p)
            FROM ProductJpaEntity p
            WHERE
                (:search =''
                    OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:activeOnly IS NULL OR p.active = :activeOnly)
                AND (
                    :lowStockOnly IS NULL
                    OR (:lowStockOnly = TRUE
                        AND p.quantityOnHand <= p.reorderLevel)
                    OR (:lowStockOnly = FALSE
                        AND p.quantityOnHand > p.reorderLevel)
                )
            """
    )
    Page<ProductJpaEntity> searchProducts(
            @Param("search") String search,
            @Param("activeOnly") Boolean activeOnly,
            @Param("lowStockOnly") Boolean lowStockOnly,
            Pageable pageable
    );
}
