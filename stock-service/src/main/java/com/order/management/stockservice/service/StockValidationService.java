package com.order.management.stockservice.service;

import com.order.management.stockservice.dto.OrderMessage;

public interface StockValidationService {
    void consumeOrder(OrderMessage order);
    void rejectOrder(OrderMessage order);
    void verifyStock(OrderMessage order);
}
