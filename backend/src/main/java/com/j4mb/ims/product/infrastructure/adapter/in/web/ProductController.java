package com.j4mb.ims.product.infrastructure.adapter.in.web;

import com.j4mb.ims.product.application.port.in.*;
import com.j4mb.ims.product.domain.Product;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
   private final UpdateProductUseCase updateProductUseCase;
private final DeleteProductUseCase deleteProductUseCase;
    private final ListProductsUseCase  listProductsUseCase;
    public ProductController(
            CreateProductUseCase createProductUseCase,GetProductUseCase getProductUseCase,
          UpdateProductUseCase updateProductUseCase,
            DeleteProductUseCase deleteProductUseCase,
            ListProductsUseCase listProductsUseCase
            ) {
        this.createProductUseCase = createProductUseCase;
        this.getProductUseCase = getProductUseCase;
     this.deleteProductUseCase  =   deleteProductUseCase;
        this.listProductsUseCase = listProductsUseCase;
        this.updateProductUseCase = updateProductUseCase;
    }
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        Product product = createProductUseCase.createProduct(
                new CreateProductUseCase.CreateProductCommand(
                        request.sku(),
                        request.name(),
                        request.description(),
                        request.unitOfMeasure(),
                        request.unitCost(),
                        request.quantityOnHand(),
                        request.reorderLevel()
                )
        );

        URI location = URI.create("/api/products/" + product.getId());

        return ResponseEntity
                .created(location)
                .body(ProductResponse.from(product));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable Long id) {

        Product product = getProductUseCase.getProduct(id);

        return ResponseEntity.ok(ProductResponse.from(product));
    }
    @GetMapping
    public ResponseEntity<ProductPageResponse> listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(required = false) Boolean lowStockOnly) {

        ListProductsUseCase.ProductPage result =
                listProductsUseCase.listProducts(
                        new ListProductsUseCase.ListProductsQuery(
                                page,
                                size,
                                search,
                                activeOnly,
                                lowStockOnly
                        )
                );

        return ResponseEntity.ok(ProductPageResponse.from(result));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {

        Product product = updateProductUseCase.updateProduct(
                new UpdateProductUseCase.UpdateProductCommand(
                        id,
                        request.name(),
                        request.description(),
                        request.unitOfMeasure(),
                        request.unitCost(),
                        request.reorderLevel()
                )
        );

        return ResponseEntity.ok(ProductResponse.from(product));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

        deleteProductUseCase.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
}
