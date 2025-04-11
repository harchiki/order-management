package com.order.management.productservice.service;

import com.order.management.productservice.dto.ProductRequest;
import com.order.management.productservice.model.Product;

import java.util.Optional;

public interface ProductService {
    Optional<Product> findById(Long id);
    void save(Product product);
    void decreaseStock(ProductRequest request);
    void increaseStock(ProductRequest request);
}
