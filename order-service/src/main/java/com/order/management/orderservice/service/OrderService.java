package com.order.management.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.order.management.orderservice.dto.order.OrderMessage;
import com.order.management.orderservice.dto.order.OrderRequestDto;
import com.order.management.orderservice.dto.validation.ValidationResultDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public interface OrderService {
    OrderMessage validateOrder(OrderRequestDto orderDto) throws JsonProcessingException;
    void consumeValidation(ValidationResultDto validationResultDto);
}

