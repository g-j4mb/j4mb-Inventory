package com.j4mb.ims.product.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {
    @Test
    public void createValidProduct() {
        Product product=new Product(
                "sku-001",
                "laptop",
                "Core I7 G 13","piece",12, new BigDecimal("120.00"),10,true);
        assertEquals("SKU-001",product.getSKU());
        assertEquals("laptop",product.getName());
        assertEquals("Core I7 G 13",product.getDescription());
        assertEquals("PIECE",product.getUnitOfMeasure());
        assertEquals(0,new BigDecimal("120.0").compareTo( product.getUnitCost()));
        assertTrue(product.isActive());

    }
    @Test
    public void rejectBlankSKU() {
        assertThrows(IllegalArgumentException.class,()->new Product(
                " ",  "PC",
                null,
                "PIECE",10,
                new BigDecimal("120.00"),
                3,true
        ));
    }
    @Test
    public void rejectNegativeUnitCost() {
        assertThrows(IllegalArgumentException.class,()->{
            new Product(
                    "sku-001",  "PC",
                    null,
                    "PIECE",10,
                    new BigDecimal("-120.00"),
                    3,true
            );
        });
        assertThrows(NullPointerException.class,()->{
            new Product(
                    "sku-001",  "PC",
                    null,
                    "PIECE",10,
                    null,
                    3,true
            );
        });
    }
    @Test
    void shouldRejectNegativeStock() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product(
                        "SKU-001",
                        "Keyboard",
                        null,
                        "PIECE",
                        -1,
                        new BigDecimal("120.00"),
                        3,true
                )
        );
    }

    @Test
    void shouldDetectLowStock() {
        Product product = new Product(
                "SKU-001",
                "PC",
                null,
                "PIECE",3,
                new BigDecimal("120.00"),
                3,
                true
        );

        assertTrue(product.isLowStock());
    }

    @Test
    void shouldDeactivateProduct() {
        Product product = createProduct();

        product.deactivate();

        assertFalse(product.isActive());
    }

    private Product createProduct() {
        return new Product(
                "SKU-001",
                "PC",
                null,
                "PIECE",
                10,
                new BigDecimal("120.00"),
                1,
                true
        );
    }
    @Test
    void shouldUpdateProductDetails() {
        Product product = new Product(
                "SKU-001",
                "Old Name",
                "Old description",
                "PCS",20,
                new BigDecimal("10.00"),
                5,true
        );

        product.updateDetails(
                "New Name",
                "New description",
                "box",
                new BigDecimal("15.50"),
                8
        );

        assertEquals("New Name", product.getName());
        assertEquals("New description", product.getDescription());
        assertEquals("BOX", product.getUnitOfMeasure());
        assertEquals(new BigDecimal("15.50"), product.getUnitCost());
        assertEquals(8, product.getReorderedLevel());

        // Updating details must not change stock or SKU.
        assertEquals("SKU-001", product.getSKU());
        assertEquals(20, product.getQuantityOnHand());
    }

    @Test
    void shouldNotUpdateProductWhenUnitCostIsNegative() {
        Product product = new Product(
                "SKU-001",
                "Old Name",
                null,
                "PCS",
                20,
                new BigDecimal("10.00"),
                5,
                true
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> product.updateDetails(
                        "New Name",
                        null,
                        "PCS",
                        new BigDecimal("-1.00"),
                        5
                )
        );

        // Validation should happen before any fields are changed.
        assertEquals("Old Name", product.getName());
        assertEquals(new BigDecimal("10.00"), product.getUnitCost());
    }
}
