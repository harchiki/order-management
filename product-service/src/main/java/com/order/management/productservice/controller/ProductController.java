package com.order.management.productservice.controller;

import com.order.management.productservice.dto.ProductPriceDto;

import com.order.management.productservice.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductController {
    private final ProductService productService;

    @GetMapping("/price")
    public ResponseEntity<List<ProductPriceDto>> getPriceList(@RequestParam List<Long> ids) {
        List<ProductPriceDto> priceList = productService.getPriceList(ids);
        return ResponseEntity.ok(priceList);
    }
}
