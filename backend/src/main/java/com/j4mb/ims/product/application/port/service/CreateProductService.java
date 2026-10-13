package com.j4mb.ims.product.application.port.service;

import com.j4mb.ims.product.application.port.in.CreateProductUseCase;
import com.j4mb.ims.product.application.port.out.SaveProductPort;
import com.j4mb.ims.product.domain.Product;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreateProductService implements CreateProductUseCase {
private final SaveProductPort saveProductPort;
public  CreateProductService(SaveProductPort saveProductPort) {
    Objects.requireNonNull( saveProductPort, "saveProductPort is null" );
    this.saveProductPort = saveProductPort;
}
    @Override
    public Product createProduct(CreateProductCommand command) {
    Objects.requireNonNull(command, "command is null" );
        Product product= new Product(
                command.sku(),
                command.name(),
                command.description(),
                command.unitOfMeasure(),
                command.quantityOnHand(),
                command.unitCost(),
                command.reorderLevel(),
                true
        );
        return saveProductPort.save(product);
    }
}
