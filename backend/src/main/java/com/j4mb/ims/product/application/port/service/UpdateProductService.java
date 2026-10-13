package com.j4mb.ims.product.application.port.service;

import com.j4mb.ims.product.application.exception.ProductNotFoundException;
import com.j4mb.ims.product.application.port.in.UpdateProductUseCase;
import com.j4mb.ims.product.application.port.out.LoadProductPort;
import com.j4mb.ims.product.application.port.out.SaveProductPort;
import com.j4mb.ims.product.domain.Product;
import org.springframework.stereotype.Service;

import java.util.Objects;
@Service
public class UpdateProductService implements UpdateProductUseCase {
    private final LoadProductPort loadProductPort;
    private final SaveProductPort saveProductPort;
    public UpdateProductService(LoadProductPort loadProductPort, SaveProductPort saveProductPort) {
        this.loadProductPort = loadProductPort;
        this.saveProductPort = saveProductPort;
    }
    @Override
    public Product updateProduct(UpdateProductCommand updateProductCommand) {
        Objects.requireNonNull(updateProductCommand, "updateProductCommand cannot be null");
        if(updateProductCommand.productId()==null|| updateProductCommand.productId()<=0)
        {
            throw  new IllegalArgumentException("productId is invalid");
        }
        Product product= loadProductPort.findById(updateProductCommand.productId()).orElseThrow(
                ()-> new ProductNotFoundException(updateProductCommand.productId())
        );
        product.updateDetails(    updateProductCommand.name(),
                updateProductCommand.description(),
                updateProductCommand.unitOfMeasure(),
                updateProductCommand.unitCost(),
                updateProductCommand.reorderLevel()
        );
        return saveProductPort.save(product);
    }
}
