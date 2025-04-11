package com.order.management.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.order.management.orderservice.dto.order.OrderRecordDto;
import com.order.management.orderservice.dto.order.OrderRequestDto;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

import java.io.IOException;

public interface OrderService {
    OrderRecordDto preprocessOrderRequest(OrderRequestDto orderDto) throws JsonProcessingException;
    void consumeValidation(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException;
}

