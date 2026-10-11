package com.j4mb.ims.product.application.service;

import com.j4mb.ims.product.application.port.in.CreateProductUseCase;
import com.j4mb.ims.product.application.port.out.SaveProductPort;
import com.j4mb.ims.product.application.port.service.CreateProductService;
import com.j4mb.ims.product.domain.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateProductServiceTest {
    @Test
    void shouldCreateAndSaveProduct() {
        SaveProductPort savePort = mock(SaveProductPort.class);

        when(savePort.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateProductService service = new CreateProductService(savePort);

        CreateProductUseCase.CreateProductCommand command = new CreateProductUseCase.CreateProductCommand(
                "SKU-001",
                "Keyboard",
                "Mechanical keyboard",
                "PCS",
                new BigDecimal("120.00"),
                10,
                3
        );

        Product result = service.createProduct(command);

        assertEquals("SKU-001", result.getSKU());
        assertEquals("Keyboard", result.getName());
        verify(savePort).save(any(Product.class));
    }

    @Test
    void shouldNotSaveProductWhenDomainValidationFails() {
        SaveProductPort savePort = mock(SaveProductPort.class);
        CreateProductService service = new CreateProductService(savePort);

        CreateProductUseCase.CreateProductCommand command = new CreateProductUseCase.CreateProductCommand(
                " ",
                "Keyboard",
                null,
                "PCS",
                new BigDecimal("120.00"),
                10,
                3
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createProduct(command)
        );

        verifyNoInteractions(savePort);
    }
}
