package com.order.management.discountservice.service;

import com.order.management.discountservice.dto.OrderMessage;

public interface DiscountValidationService {
    void consumeOrder(OrderMessage order);
}
