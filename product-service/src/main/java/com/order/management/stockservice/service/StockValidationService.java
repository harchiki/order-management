package com.order.management.stockservice.service;

import com.order.management.stockservice.dto.OrderMessage;
import com.order.management.stockservice.dto.StockValidationResult;

public interface StockValidationService {
    void consumeOrder(OrderMessage order);
}
