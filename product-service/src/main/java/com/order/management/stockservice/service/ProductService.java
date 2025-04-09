package com.order.management.stockservice.service;

import com.order.management.stockservice.dto.ProductRequest;
import com.order.management.stockservice.model.Product;

import java.util.Optional;

public interface ProductService {
    Optional<Product> findById(Long id);
    void save(Product product);
    void decreaseStock(ProductRequest request);

    void increaseStock(ProductRequest request);
}
