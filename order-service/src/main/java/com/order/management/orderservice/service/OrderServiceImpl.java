package com.order.management.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.management.orderservice.data.OrderStatus;
import com.order.management.orderservice.dto.order.OrderRequestDto;
import com.order.management.orderservice.dto.order.Order;
import com.order.management.orderservice.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderServiceImpl implements OrderService {
    private final RabbitTemplate rabbitTemplate;
    private final OrderMapper orderMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Order publishPayment(OrderRequestDto orderDto) throws JsonProcessingException {
        Order order = orderMapper.orderRequestDtoToOrder(orderDto);
        order.setStatus(OrderStatus.CREATED);
        var json = objectMapper.writeValueAsString(order);

        rabbitTemplate.convertAndSend("x.order.validation", "", order);
        return order;
    }
}

