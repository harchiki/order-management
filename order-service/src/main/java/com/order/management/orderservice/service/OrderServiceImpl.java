package com.order.management.orderservice.service;

import com.order.management.common.constant.OrderStatus;
import com.order.management.common.constant.ValidationType;
import com.order.management.orderservice.dto.order.OrderMessage;
import com.order.management.orderservice.dto.order.OrderRequestDto;
import com.order.management.orderservice.dto.validation.ValidationResult;
import com.order.management.orderservice.dto.validation.ValidationResultDto;
import com.order.management.orderservice.mapper.OrderMapper;

import com.order.management.orderservice.model.Order;
import com.order.management.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderServiceImpl implements OrderService {
    private final RabbitTemplate rabbitTemplate;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    // key order id
    private final RedisTemplate<UUID, ValidationResult> redisTemplate;


    @Value("${order.validation.exchange}")
    private String orderValidationExchange;

    @Override
    public OrderMessage validateOrder(OrderRequestDto orderDto) {
        Order order = new Order();
        orderMapper.orderRequestDtoToOrder(order, orderDto);
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);

        OrderMessage orderMessage = orderMapper.orderToOrderMessage(order);
        rabbitTemplate.convertAndSend(orderValidationExchange, "", orderMessage);
        return orderMessage;
    }

    @RabbitListener(queues = "${order.validation.response.queue}")
    @Override
    public void consumeValidation(ValidationResultDto validationResultDto) {
        log.info("Consuming validation result : {}", validationResultDto);

        UUID orderId = validationResultDto.getOrderId();

        ValidationResult result = Optional.ofNullable(redisTemplate.opsForValue().get(orderId))
                .orElse(new ValidationResult(orderId));

        switch (validationResultDto.getValidationType()) {
            case ValidationType.DISCOUNT_CODE -> result.setDiscountStatus(validationResultDto.getStatus());
            case ValidationType.STOCK -> result.setStockStatus(validationResultDto.getStatus());
        }

        if (Optional.ofNullable(result.getDiscountStatus()).isEmpty()
                || Optional.ofNullable(result.getStockStatus()).isEmpty()) {
            redisTemplate.opsForValue().set(orderId, result);
            log.info("Cached validation result : {}", result);
            return;
        }
        // todo rejecting order

        // todo redirect to calculate cost
        redisTemplate.delete(orderId);
        log.info("Removed validation result from cache : {}", result);
    }
}

