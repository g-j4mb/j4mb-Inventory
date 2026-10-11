package com.j4mb.ims.product.application.service;

import com.j4mb.ims.product.application.exception.ProductNotFoundException;
import com.j4mb.ims.product.application.port.out.LoadProductPort;
import com.j4mb.ims.product.application.port.service.GetProductService;
import com.j4mb.ims.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GetProductServiceTest {
    private LoadProductPort loadProductPort;
    private GetProductService service;

    @BeforeEach
    void setUp() {
        loadProductPort = mock(LoadProductPort.class);
        service = new GetProductService(loadProductPort);
    }

    @Test
    void shouldReturnProductWhenFound() {
        Product product = new Product(
                1L, "SKU-001", "Keyboard", "USB keyboard",
                "PCS", new BigDecimal("25.00"), 20, 5, true
        );

        when(loadProductPort.findById(1L))
                .thenReturn(Optional.of(product));

        Product result = service.getProduct(1L);

        assertSame(product, result);
        assertEquals("SKU-001", result.getSKU());
        verify(loadProductPort).findById(1L);
    }

    @Test
    void shouldThrowWhenProductDoesNotExist() {
        when(loadProductPort.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service.getProduct(99L)
        );
    }

    @Test
    void shouldRejectInvalidProductId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.getProduct(0L)
        );

        verifyNoInteractions(loadProductPort);
    }
}
