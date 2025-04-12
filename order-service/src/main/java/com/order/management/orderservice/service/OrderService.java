package com.order.management.orderservice.service;

import com.order.management.common.constant.OrderStatus;
import com.order.management.orderservice.dto.order.OrderRecordDto;
import com.order.management.orderservice.dto.order.OrderRequestDto;
import com.order.management.orderservice.model.Order;

import java.util.UUID;

public interface OrderService {
    Order createOrderRequest(OrderRequestDto orderDto);

    OrderRecordDto getOrder(UUID orderId);

    void updateOrderStatus(UUID orderId, OrderStatus status);
}
