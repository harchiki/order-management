package com.order.management.discountservice.service;

import com.order.management.discountservice.dto.DiscountResponseDto;
import com.order.management.discountservice.dto.OrderMessage;

public interface DiscountService {
    void consumeOrder(OrderMessage order);
    DiscountResponseDto getDiscount(String discountCode);
}
