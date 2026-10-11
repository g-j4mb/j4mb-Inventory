package com.j4mb.ims.product.application.service;

import com.j4mb.ims.product.application.exception.ProductNotFoundException;
import com.j4mb.ims.product.application.port.in.UpdateProductUseCase;
import com.j4mb.ims.product.application.port.out.LoadProductPort;
import com.j4mb.ims.product.application.port.out.SaveProductPort;
import com.j4mb.ims.product.application.port.service.UpdateProductService;
import com.j4mb.ims.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UpdateProductServiceTest {
    private LoadProductPort loadProductPort;
    private SaveProductPort saveProductPort;
    private UpdateProductService service;

    @BeforeEach
    void setUp() {
        loadProductPort = mock(LoadProductPort.class);
        saveProductPort = mock(SaveProductPort.class);

        service = new UpdateProductService(
                loadProductPort,
                saveProductPort
        );
    }

    @Test
    void shouldUpdateAndSaveExistingProduct() {
        Product existingProduct = new Product(
                1L,
                "SKU-001",
                "Old Name",
                "Old description",
                "PCS",

                new BigDecimal("10.00"),
               20,
               5,
                true
        );

        UpdateProductUseCase.UpdateProductCommand command = new UpdateProductUseCase.UpdateProductCommand(
                1L,
                "New Name",
                "New description",
                "box",
                new BigDecimal("15.50"),
                8
        );

        when(loadProductPort.findById(1L))
                .thenReturn(Optional.of(existingProduct));
        when(saveProductPort.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service.updateProduct(command);

        assertEquals("New Name", result.getName());
        assertEquals("New description", result.getDescription());
        assertEquals("BOX", result.getUnitOfMeasure());
        assertEquals(new BigDecimal("15.50"), result.getUnitCost());
        assertEquals(8, result.getReorderLevel());

        // Updating details must preserve identity and stock.
        assertEquals(1L, result.getId());
        assertEquals("SKU-001", result.getSKU());
        assertEquals(20, result.getQuantityOnHand());

        verify(loadProductPort).findById(1L);
        verify(saveProductPort).save(existingProduct);
    }

    @Test
    void shouldThrowWhenProductDoesNotExist() {
        when(loadProductPort.findById(99L))
                .thenReturn(Optional.empty());

        UpdateProductUseCase.UpdateProductCommand command = new UpdateProductUseCase.UpdateProductCommand(
                99L,
                "New Name",
                null,
                "PCS",
                new BigDecimal("10.00"),
                5
        );

        assertThrows(
                ProductNotFoundException.class,
                () -> service.updateProduct(command)
        );

        verify(saveProductPort, never()).save(any(Product.class));
    }

    @Test
    void shouldNotSaveWhenUpdatedDetailsAreInvalid() {
        Product existingProduct = new Product(
                1L,
                "SKU-001",
                "Old Name",
                null,
                "PCS",
                new BigDecimal("10.00"),
                20,
                5,
                true
        );

        when(loadProductPort.findById(1L))
                .thenReturn(Optional.of(existingProduct));

        UpdateProductUseCase.UpdateProductCommand command = new UpdateProductUseCase.UpdateProductCommand(
                1L,
                "New Name",
                null,
                "PCS",
                new BigDecimal("-1.00"),
                5
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateProduct(command)
        );

        assertEquals("Old Name", existingProduct.getName());
        assertEquals(new BigDecimal("10.00"), existingProduct.getUnitCost());

        verify(saveProductPort, never()).save(any(Product.class));
    }

    @Test
    void shouldRejectInvalidProductId() {
        UpdateProductUseCase.UpdateProductCommand command = new UpdateProductUseCase.UpdateProductCommand(
                0L,
                "New Name",
                null,
                "PCS",
                new BigDecimal("10.00"),
                5
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateProduct(command)
        );

        verifyNoInteractions(loadProductPort, saveProductPort);
    }
}
