package com.order.management.discountservice.controller;

import com.order.management.discountservice.dto.DiscountResponseDto;
import com.order.management.discountservice.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/discount")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class DiscountController {
    private final DiscountService discountService;

    @GetMapping
    public ResponseEntity<DiscountResponseDto> getDiscount(@RequestParam(name = "discount-code", required = false) String discountCode) {
        DiscountResponseDto discount = discountService.getDiscount(discountCode);
        return ResponseEntity.ok(discount);
    }
}
