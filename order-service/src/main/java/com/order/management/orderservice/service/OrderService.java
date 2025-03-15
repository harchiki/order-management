package com.order.management.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.order.management.orderservice.dto.order.OrderRequestDto;
import com.order.management.orderservice.dto.order.Order;

public interface OrderService {
    Order publishPayment(OrderRequestDto orderDto) throws JsonProcessingException;
}

