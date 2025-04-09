package com.order.management.stockservice.service;

import com.order.management.stockservice.dto.OrderRequestDto;

public interface StockService {
    void consumeOrder(OrderRequestDto order);
}
