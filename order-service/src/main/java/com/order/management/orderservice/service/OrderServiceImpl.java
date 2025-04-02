package com.order.management.orderservice.service;

import com.order.management.common.constant.OrderStatus;
import com.order.management.orderservice.dto.order.OrderMessage;
import com.order.management.orderservice.dto.order.OrderRequestDto;
import com.order.management.orderservice.mapper.OrderMapper;

import com.order.management.orderservice.model.Order;
import com.order.management.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderServiceImpl implements OrderService {
    private final RabbitTemplate rabbitTemplate;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;

    @Value("${order.validation.exchange}")
    private String orderValidationExchange;

    @Override
    public OrderMessage producePayment(OrderRequestDto orderDto) {
        Order order = new Order();
        orderMapper.orderRequestDtoToOrder(order, orderDto);
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);

        OrderMessage orderMessage = orderMapper.orderToOrderMessage(order);
        rabbitTemplate.convertAndSend(orderValidationExchange, "", orderMessage);
        return orderMessage;
    }
}

