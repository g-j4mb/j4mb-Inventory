package com.j4mb.ims.product.infrastructure.adapter.in.web;

import com.j4mb.ims.product.application.exception.ProductNotFoundException;
import com.j4mb.ims.product.application.port.in.*;
import com.j4mb.ims.product.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CreateProductUseCase createProductUseCase;
    @MockitoBean
    private GetProductUseCase getProductUseCase;
    @MockitoBean
    private ListProductsUseCase  listProductsUseCase;
    @MockitoBean
    private DeleteProductUseCase deleteProductUseCase;
    @MockitoBean
    private UpdateProductUseCase updateProductUseCase;



    @Test
    void shouldCreateProductAndReturn201() throws Exception {
        Product product= new Product(
                1L,
                "KB-100",
                "Mechanical Keyboard",
                "USB keyboard",
                "PCS",
                new BigDecimal("120.00"),
                25,
                5,
                true
        );
        when(createProductUseCase.createProduct(any())).thenReturn(product);
        mockMvc.perform(
                post("/api/products").contentType("application/json")
                .content("""
                {
                        "sku": "KB-100",
                "name": "Mechanical Keyboard",
                "description": "USB keyboard",
                "unitOfMeasure": "PCS",
                "unitCost": 120.00,
                "quantityOnHand": 25,
                "reorderLevel": 5
                                }
              """)).andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location", "/api/products/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sku").value("KB-100"))
                .andExpect(jsonPath("$.name")
                        .value("Mechanical Keyboard"))
                .andExpect(jsonPath("$.active").value(true));
    }
    @Test
    void shouldRejectInvalidProductRequest() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content("""
                                {
                                  "sku": "",
                                  "name": "",
                                  "unitOfMeasure": "PCS",
                                  "unitCost": 120.00,
                                  "quantityOnHand": 25,
                                  "reorderLevel": 5
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
    @Test
    void shouldGetProductAndReturn200() throws Exception {
        Product product = new Product(
                1L,
                "KB-100",
                "Mechanical Keyboard",
                "USB keyboard",
                "PCS",
                new BigDecimal("120.00"),
                25,
                5,
                true
        );

        when(getProductUseCase.getProduct(1L))
                .thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sku").value("KB-100"))
                .andExpect(jsonPath("$.name")
                        .value("Mechanical Keyboard"))
                .andExpect(jsonPath("$.quantityOnHand").value(25));
    }
    @Test
    void shouldListProductsWithPagination() throws Exception {
        Product product = new Product(
                1L,
                "KB-100",
                "Mechanical Keyboard",
                "USB keyboard",
                "PCS",
                new BigDecimal("120.00"),
                25,
                5,
                true
        );

        ListProductsUseCase.ProductPage page =
                new ListProductsUseCase.ProductPage(
                        List.of(product),
                        0,
                        20,
                        1,
                        1
                );

        when(listProductsUseCase.listProducts(any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products.length()").value(1))
                .andExpect(jsonPath("$.products[0].sku").value("KB-100"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }
    @Test
    void shouldUpdateProductAndReturn200() throws Exception {
        Product updated = new Product(
                1L,
                "KB-100",
                "Mechanical Keyboard Pro",
                "Updated USB keyboard",
                "PCS",
                new BigDecimal("135.00"),
                25,
                8,
                true
        );

        when(updateProductUseCase.updateProduct(any()))
                .thenReturn(updated);

        mockMvc.perform(put("/api/products/1")
                        .contentType("application/json")
                        .content("""
                            {
                              "name": "Mechanical Keyboard Pro",
                              "description": "Updated USB keyboard",
                              "unitOfMeasure": "PCS",
                              "unitCost": 135.00,
                              "reorderLevel": 8
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Mechanical Keyboard Pro"))
                .andExpect(jsonPath("$.quantityOnHand").value(25))
                .andExpect(jsonPath("$.reorderLevel").value(8));
    }

    @Test
    void shouldDeleteProductAndReturn204() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
    @Test
    void shouldReturn404WhenProductDoesNotExist() throws Exception {
        when(getProductUseCase.getProduct(999L))
                .thenThrow(new ProductNotFoundException(999L));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/products/999"));
    }
}
