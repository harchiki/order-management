package com.order.management.stockservice.service.impl;

import com.order.management.stockservice.dto.ProductRequest;
import com.order.management.stockservice.model.Product;
import com.order.management.stockservice.repository.ProductRepository;
import com.order.management.stockservice.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;

    @Override
    public Optional<Product> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void save(Product product) {
        repository.save(product);
    }

    @Override
    public void decreaseStock(ProductRequest request) {
        Optional<Product> optProduct = repository.findById(request.getProductId());
        if (optProduct.isPresent()) {
            Product product = optProduct.get();
            product.setQuantity(product.getQuantity() - request.getQuantity());
            repository.save(product);
        }
    }

    @Override
    public void increaseStock(ProductRequest request) {
        Optional<Product> optProduct = repository.findById(request.getProductId());
        if (optProduct.isPresent()) {
            Product product = optProduct.get();
            product.setQuantity(product.getQuantity() - request.getQuantity());
            repository.save(product);
        }
    }
}
