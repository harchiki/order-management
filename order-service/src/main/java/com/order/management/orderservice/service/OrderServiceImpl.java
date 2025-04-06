package com.order.management.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.management.common.constant.OrderStatus;
import com.order.management.common.constant.ValidationStatus;
import com.order.management.common.constant.ValidationType;
import com.order.management.orderservice.dto.order.OrderMessage;
import com.order.management.orderservice.dto.order.OrderRequestDto;
import com.order.management.orderservice.dto.validation.RejectedOrder;
import com.order.management.orderservice.dto.validation.ValidationResult;
import com.order.management.orderservice.dto.validation.ValidationResultDto;
import com.order.management.orderservice.helper.rabbitmq.UnprocessableMessageHandler;
import com.order.management.orderservice.mapper.OrderMapper;

import com.order.management.orderservice.model.Order;
import com.order.management.orderservice.repository.OrderRepository;
import com.order.management.orderservice.util.CacheUtil;
import com.rabbitmq.client.Channel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderServiceImpl implements OrderService {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    // key order id
    private final RedisTemplate<String, ValidationResult> validationCacheTemplate;
    private UnprocessableMessageHandler unprocessableMessageHandler;

    @Value("${order.validation.response.dlx}")
    private String rejectedOrderExchange;

    @Value("${order.validation.exchange}")
    private String orderValidationExchange;

    @PostConstruct
    private void postConstruct() {
        unprocessableMessageHandler = new UnprocessableMessageHandler(rejectedOrderExchange);
    }

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

    @RabbitListener(queues = "${order.validation.response.queue}", containerFactory = "customRabbitListener")
    @Override
    public void consumeValidation(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        ValidationResultDto validationResultDto = objectMapper.readValue(message.getBody(), ValidationResultDto.class);
        log.info("Consuming validation result : {}", validationResultDto);

        final UUID orderId = validationResultDto.getOrderId();
        final String validationCacheKey = CacheUtil.getCacheKey(CacheUtil.VALIDATION_CACHE_ORDER_KEY, orderId);
        // check if any validation record exists in cache
        ValidationResult result = Optional.ofNullable(validationCacheTemplate.opsForValue().get(validationCacheKey))
                .orElse(new ValidationResult(orderId));

        if (result.isRejected()) {
            // check whether the order is already rejected from cache
            log.info("Order is already rejected, orderId : {}", orderId);
            channel.basicAck(deliveryTag, false);
        } else if (isRejected(result)) {
            // check if order will be rejected
            log.info("Order is rejected, orderId : [{}]", result);
            RejectedOrder rejectedOrder = new RejectedOrder(orderId, getRejectionReason(result));
            rabbitTemplate.convertAndSend(rejectedOrderExchange, "", rejectedOrder);

            // set isRejected = true and cache it, in case other validation message
            result.setRejected(true);
            validationCacheTemplate.opsForValue().set(validationCacheKey, result, Duration.ofMinutes(1));
            channel.basicAck(deliveryTag, false);

        } else if (isReadyToProceed(result) && !result.isProceeded()) {
            // ready to be proceeded
            log.info("Validated the order, orderId : {}", result.getOrderId());

            // todo redirect it to calculate cost
            // rabbitTemplate.convertAndSend();
            log.info("Proceeded it to calculate total cost, orderId : {}", result.getOrderId());

            // set isProceeded = true and cache it, in case other validation message
            result.setProceeded(true);
            validationCacheTemplate.opsForValue().set(validationCacheKey, result, Duration.ofMinutes(1));
            channel.basicAck(deliveryTag, false);
        } else {
            // map new results to ValidationResult from dto
            mapResultDtoToResult(validationResultDto, result);

            if (!isReadyToProceed(result) && !result.isRejected()) {
                validationCacheTemplate.opsForValue().set(validationCacheKey, result, Duration.ofMinutes(1));
                log.info("Cached validation result : {}", result);

                unprocessableMessageHandler.handleErrorProcessingMessage(message, channel, deliveryTag);
            } else if (result.isRejected()) {
                // check whether the order is already rejected from cache
                log.info("Order is already rejected, orderId : {}", orderId);
                channel.basicAck(deliveryTag, false);
            } else {
                log.info("Ready to proceed, it will be proceeded as soon, orderId : {}", orderId);
                validationCacheTemplate.opsForValue().set(validationCacheKey, result, Duration.ofMinutes(1));
                channel.basicAck(deliveryTag, false);
            }
        }
    }

    private void mapResultDtoToResult(ValidationResultDto validationResultDto, ValidationResult result) {
        // map stock status if exists
        Optional.ofNullable(validationResultDto.getStockStatus())
                .ifPresent(result::setStockStatus);

        // map discount status if exists
        Optional.ofNullable(validationResultDto.getDiscountStatus())
                .ifPresent(result::setDiscountStatus);
    }

    private boolean isRejected(ValidationResult result) {
        return Arrays.asList(result.getDiscountStatus(), result.getStockStatus())
                .contains(ValidationStatus.REJECTED);
    }

    private boolean isReadyToProceed(ValidationResult result) {
        return ValidationStatus.OKAY.equals(result.getStockStatus())
                && ValidationStatus.OKAY.equals(result.getDiscountStatus());
    }

    private String getRejectionReason(ValidationResult result) {
        if (ValidationStatus.REJECTED.equals(result.getDiscountStatus())) return ValidationType.DISCOUNT_CODE.name();
        else if (ValidationStatus.REJECTED.equals(result.getStockStatus())) return  ValidationType.STOCK.name();
        else return "Unknown Reason";
    }
}

