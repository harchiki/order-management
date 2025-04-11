package com.order.management.productservice.service.impl;

import com.order.management.productservice.dto.ProductPriceDto;
import com.order.management.productservice.dto.ProductRequest;
import com.order.management.productservice.mapper.ProductMapper;
import com.order.management.productservice.model.Product;
import com.order.management.productservice.repository.ProductRepository;
import com.order.management.productservice.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final ProductMapper productMapper;

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
            product.setQuantity(product.getQuantity() + request.getQuantity());
            repository.save(product);
        }
    }

    public List<ProductPriceDto> getPriceList(List<Long> ids) {
        List<Product> products = repository.findAllById(ids);
        return productMapper.productToProductPriceDto(products);
    }
}
