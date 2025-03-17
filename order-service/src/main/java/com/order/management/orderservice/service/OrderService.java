package com.order.management.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.order.management.orderservice.dto.order.OrderMessage;
import com.order.management.orderservice.dto.order.OrderRequestDto;

public interface OrderService {
    OrderMessage producePayment(OrderRequestDto orderDto) throws JsonProcessingException;
}

