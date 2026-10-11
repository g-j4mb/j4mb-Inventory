package com.j4mb.ims.product.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import com.j4mb.ims.product.domain.Product;

@Entity
@Table(name="products")
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String sku;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "unit_of_measure", nullable = false, length = 20)
    private String unitOfMeasure;

    @Column(name = "unit_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "quantity_on_hand", nullable = false)
    private int quantityOnHand;

    @Column(name = "reorder_level", nullable = false)
    private int reorderLevel;

    @Column(nullable = false)
    private boolean active;

    protected ProductJpaEntity() {
        // Required by JPA.
    }

    public static ProductJpaEntity fromDomain(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.copyFrom(product);
        return entity;
    }

    public void copyFrom(Product product) {
        this.sku = product.getSKU();
        this.name = product.getName();
        this.description = product.getDescription();
        this.unitOfMeasure = product.getUnitOfMeasure();
        this.unitCost = product.getUnitCost();
        this.quantityOnHand = product.getQuantityOnHand();
        this.reorderLevel = product.getReorderLevel();
        this.active = product.isActive();
    }

    public Product toDomain() {
        return new Product(
                id,
                sku,
                name,
                description,
                unitOfMeasure,
                unitCost,
                quantityOnHand,
                reorderLevel,
                active
        );
    }

    public Long getId() {
        return id;
    }
}
