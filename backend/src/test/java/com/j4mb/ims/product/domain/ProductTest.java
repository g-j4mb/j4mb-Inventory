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
}
