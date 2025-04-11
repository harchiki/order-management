package com.order.management.orderservice.service.impl;

import com.order.management.common.constant.OrderStatus;
import com.order.management.orderservice.dto.order.OrderRequestDto;
import com.order.management.orderservice.mapper.OrderMapper;
import com.order.management.orderservice.model.Order;
import com.order.management.orderservice.model.ProductRequest;
import com.order.management.orderservice.repository.OrderRepository;
import com.order.management.orderservice.service.OrderPreprocessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderPreprocessServiceImpl implements OrderPreprocessService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;

    @Override
    public Order createOrderRequest(OrderRequestDto orderDto) {
        Order order = new Order();
        orderMapper.orderRequestDtoToOrder(order, orderDto);
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);
        return order;
    }
}
