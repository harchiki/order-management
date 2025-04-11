package com.order.management.orderservice.service;

import com.order.management.orderservice.dto.order.OrderRequestDto;
import com.order.management.orderservice.model.Order;

public interface OrderPreprocessService {
    Order createOrderRequest(OrderRequestDto orderDto);
}
