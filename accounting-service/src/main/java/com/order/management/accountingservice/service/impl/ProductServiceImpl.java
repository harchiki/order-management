package com.order.management.accountingservice.service.impl;

import com.order.management.accountingservice.service.external.dto.ProductPriceDto;
import com.order.management.accountingservice.service.ProductService;
import com.order.management.accountingservice.service.external.ProductClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductServiceImpl implements ProductService {
    private final ProductClient productClient;

    @Override
    public List<ProductPriceDto> getPriceList(List<Long> ids) {
        ResponseEntity<List<ProductPriceDto>> response = productClient.getPriceList(ids);
        return response.getBody();
    }
}
