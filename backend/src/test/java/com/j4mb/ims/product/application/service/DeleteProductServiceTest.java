package com.j4mb.ims.product.application.service;

import com.j4mb.ims.product.application.exception.ProductNotFoundException;
import com.j4mb.ims.product.application.port.out.LoadProductPort;
import com.j4mb.ims.product.application.port.out.SaveProductPort;
import com.j4mb.ims.product.application.port.service.DeleteProductService;
import com.j4mb.ims.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DeleteProductServiceTest {
    private LoadProductPort loadProductPort;
    private SaveProductPort saveProductPort;
    private DeleteProductService service;

    @BeforeEach
    void setUp() {
        loadProductPort = mock(LoadProductPort.class);
        saveProductPort = mock(SaveProductPort.class);

        service = new DeleteProductService(
                loadProductPort,
                saveProductPort
        );
    }

    @Test
    void shouldDeactivateAndSaveExistingProduct() {
        Product product = new Product(
                1L, "SKU-001", "Keyboard", null,
                "PCS", new BigDecimal("25.00"), 20, 5, true
        );

        when(loadProductPort.findById(1L))
                .thenReturn(Optional.of(product));
        when(saveProductPort.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.deleteProduct(1L);

        assertFalse(product.isActive());
        assertEquals(20, product.getQuantityOnHand());
        verify(saveProductPort).save(product);
    }

    @Test
    void shouldThrowWhenProductDoesNotExist() {
        when(loadProductPort.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service.deleteProduct(99L)
        );

        verify(saveProductPort, never()).save(any(Product.class));
    }

    @Test
    void shouldRejectInvalidProductId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.deleteProduct(0L)
        );

        verifyNoInteractions(loadProductPort, saveProductPort);
    }

    @Test
    void shouldRemainInactiveWhenDeletedTwice() {
        Product product = new Product(
                1L, "SKU-001", "Keyboard", null,
                "PCS", new BigDecimal("25.00"), 20, 5, true
        );

        when(loadProductPort.findById(1L))
                .thenReturn(Optional.of(product));

        service.deleteProduct(1L);
        service.deleteProduct(1L);

        assertFalse(product.isActive());
        verify(saveProductPort, times(2)).save(product);
    }
}

