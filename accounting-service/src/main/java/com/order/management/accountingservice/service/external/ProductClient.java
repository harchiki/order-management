package com.order.management.accountingservice.service.external;

import com.order.management.accountingservice.service.external.dto.ProductPriceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "product-service", url = "http://localhost:8082/product")
public interface ProductClient {
    @RequestMapping(method = RequestMethod.GET, value = "/price")
    ResponseEntity<List<ProductPriceDto>> getPriceList(@RequestParam List<Long> ids);
}
