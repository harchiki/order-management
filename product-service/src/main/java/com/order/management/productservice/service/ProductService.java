package com.order.management.productservice.service;

import com.order.management.productservice.dto.ProductPriceDto;
import com.order.management.productservice.dto.ProductRequest;
import com.order.management.productservice.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductService {
    Optional<Product> findById(Long id);
    void save(Product product);
    void decreaseStock(ProductRequest request);
    void increaseStock(ProductRequest request);
    List<ProductPriceDto> getPriceList(List<Long> ids);
}
