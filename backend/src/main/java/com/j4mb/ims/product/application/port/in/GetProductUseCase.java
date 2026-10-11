package com.j4mb.ims.product.application.port.in;

import com.j4mb.ims.product.domain.Product;

public interface GetProductUseCase {

    Product getProduct(Long productId);
}