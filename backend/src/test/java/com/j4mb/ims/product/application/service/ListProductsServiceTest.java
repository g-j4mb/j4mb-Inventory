package com.j4mb.ims.product.application.service;

import com.j4mb.ims.product.application.port.in.ListProductsUseCase;
import com.j4mb.ims.product.application.port.out.LoadProductsPort;
import com.j4mb.ims.product.application.port.service.ListProductsService;
import com.j4mb.ims.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ListProductsServiceTest {
    private LoadProductsPort loadProductsPort;
    private ListProductsService service;

    @BeforeEach
    void setUp() {
        loadProductsPort = mock(LoadProductsPort.class);
        service = new ListProductsService(loadProductsPort);

    }

    @Test
    void shouldReturnRequestedProductPage() {
        Product product = new Product(
                1L, "SKU-001", "Keyboard", null,
                "PCS", new BigDecimal("25.00"), 20, 5, true
        );

        when(loadProductsPort.loadProducts(any(LoadProductsPort.ProductQueryData.class)))
                .thenReturn(new LoadProductsPort.ProductPageData(
                        List.of(product), 0, 20, 41, 3
                ));

        ListProductsUseCase.ProductPage result = service.listProducts(
                new ListProductsUseCase.ListProductsQuery(0, 20, "keyboard", true, null)
        );

        assertEquals(List.of(product), result.products());
        assertEquals(0, result.page());
        assertEquals(20, result.size());
        assertEquals(41, result.totalElements());
        assertEquals(3, result.totalPages());

        ArgumentCaptor<LoadProductsPort.ProductQueryData> captor =
                ArgumentCaptor.forClass(LoadProductsPort.ProductQueryData.class);

        verify(loadProductsPort).loadProducts(captor.capture());

        assertEquals(0, captor.getValue().page());
        assertEquals(20, captor.getValue().size());
        assertEquals("keyboard", captor.getValue().search());
        assertEquals(true, captor.getValue().activeOnly());
        assertNull(captor.getValue().lowStockOnly());
    }

    @Test
    void shouldNormalizeBlankSearchToNull() {
        when(loadProductsPort.loadProducts(any(LoadProductsPort.ProductQueryData.class)))
                .thenReturn(new LoadProductsPort.ProductPageData(
                        List.of(), 0, 10, 0, 0
                ));

        service.listProducts(
                new ListProductsUseCase.ListProductsQuery(0, 10, "   ", null, null)
        );

        ArgumentCaptor<LoadProductsPort.ProductQueryData> captor =
                ArgumentCaptor.forClass(LoadProductsPort.ProductQueryData.class);

        verify(loadProductsPort).loadProducts(captor.capture());
        assertNull(captor.getValue().search());
    }

    @Test
    void shouldRejectNegativePage() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.listProducts(
                        new ListProductsUseCase.ListProductsQuery(-1, 10, null, null, null)
                )
        );

        verifyNoInteractions(loadProductsPort);
    }

    @Test
    void shouldRejectInvalidPageSize() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.listProducts(
                        new ListProductsUseCase.ListProductsQuery(0, 0, null, null, null)
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.listProducts(
                        new ListProductsUseCase.ListProductsQuery(0, 101, null, null, null)
                )
        );

        verifyNoInteractions(loadProductsPort);
    }
}
