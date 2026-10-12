package com.j4mb.ims.product.infrastructure.adapter.out.persistence;

import com.j4mb.ims.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

@Testcontainers
class ProductPersistenceTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("POSTGRES_URL", POSTGRES::getJdbcUrl);
        registry.add("POSTGRES_USER", POSTGRES::getUsername);
        registry.add("POSTGRES_PASSWORD", POSTGRES::getPassword);
    }

    @Autowired
    private ProductPersistenceAdapter adapter;

    @Autowired
    private ProductRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void shouldApplyFlywayMigration() {
        Integer migrationCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM flyway_schema_history
                WHERE version = '1'
                  AND success = TRUE
                """,
                Integer.class
        );

        assertEquals(1, migrationCount);
    }

    @Test
    void shouldSaveAndLoadProduct() {
        Product product = new Product(
                "SKU-001",
                "Keyboard",
                "USB keyboard",
                "PCS",20,
                new BigDecimal("25.00"),
                5,true
        );

        Product saved = adapter.save(product);

        assertNotNull(saved.getId());

        Product loaded = adapter.findById(saved.getId())
                .orElseThrow();

        assertEquals(saved.getId(), loaded.getId());
        assertEquals("SKU-001", loaded.getSKU());
        assertEquals("Keyboard", loaded.getName());
        assertEquals("USB keyboard", loaded.getDescription());
        assertEquals("PCS", loaded.getUnitOfMeasure());
        assertEquals(new BigDecimal("25.00"), loaded.getUnitCost());
        assertEquals(20, loaded.getQuantityOnHand());
        assertEquals(5, loaded.getReorderLevel());
        assertTrue(loaded.isActive());
    }

    @Test
    void shouldPersistUpdatedProductDetails() {
        Product saved = adapter.save(new Product(
                "SKU-002",
                "Old Name",
                null,
                "PCS",12,
                new BigDecimal("10.00"),

                3,true
        ));

        saved.updateDetails(
                "New Name",
                "Updated description",
                "BOX",
                new BigDecimal("15.50"),
                4
        );

        adapter.save(saved);

        Product loaded = adapter.findById(saved.getId())
                .orElseThrow();

        assertEquals("New Name", loaded.getName());
        assertEquals("Updated description", loaded.getDescription());
        assertEquals("BOX", loaded.getUnitOfMeasure());
        assertEquals(new BigDecimal("15.50"), loaded.getUnitCost());
        assertEquals(4, loaded.getReorderLevel());

        // Updating details must preserve stock and SKU.
        assertEquals("SKU-002", loaded.getSKU());
        assertEquals(12, loaded.getQuantityOnHand());
    }

    @Test
    void shouldPersistDeactivationWithoutDeletingProduct() {
        Product saved = adapter.save(new Product(
                "SKU-003",
                "Mouse",
                null,
                "PCS",8,
                new BigDecimal("12.00"),

                2,true
        ));

        saved.deactivate();
        adapter.save(saved);

        Product loaded = adapter.findById(saved.getId())
                .orElseThrow();

        assertFalse(loaded.isActive());
        assertEquals(8, loaded.getQuantityOnHand());
        assertEquals(1, repository.count());
    }
}
