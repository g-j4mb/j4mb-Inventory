package com.j4mb.ims.product.application.port.out;

import com.j4mb.ims.product.domain.Product;

public interface SaveProductPort {
    Product save(Product product);
}
