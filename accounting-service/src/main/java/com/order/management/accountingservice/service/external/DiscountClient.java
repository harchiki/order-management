package com.order.management.accountingservice.service.external;

import com.order.management.accountingservice.service.external.dto.DiscountResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "discount-service", url = "http://localhost:8083/discount")
public interface DiscountClient {
    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<DiscountResponseDto> getDiscountDetail(@RequestParam(name = "discount-code") String discountCode);
}
