package com.order.management.orderservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.management.common.constant.OrderStatus;
import com.order.management.common.constant.ValidationStatus;
import com.order.management.common.constant.ValidationType;
import com.order.management.orderservice.dto.StatusUpdateDto;
import com.order.management.orderservice.dto.order.OrderRecordDto;
import com.order.management.orderservice.dto.order.OrderRequestDto;
import com.order.management.orderservice.dto.validation.RejectedOrder;
import com.order.management.orderservice.dto.validation.ValidationResult;
import com.order.management.orderservice.dto.validation.ValidationResultDto;
import com.order.management.orderservice.helper.rabbitmq.UnprocessableMessageHandler;
import com.order.management.orderservice.mapper.OrderMapper;

import com.order.management.orderservice.model.Order;
import com.order.management.orderservice.service.OrderService;
import com.order.management.orderservice.service.OrderManagementService;
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
public class OrderManagementServiceImpl implements OrderManagementService {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final OrderMapper orderMapper;
    private final OrderService orderService;

    // key order id
    private final RedisTemplate<String, ValidationResult> validationCacheTemplate;
    private UnprocessableMessageHandler unprocessableMessageHandler;

    @Value("${order.validation.response.dlx}")
    private String rejectedOrderExchange;

    @Value("${order.validation.exchange}")
    private String orderValidationExchange;

    @Value("${order.accounting.price.queue}")
    private String priceQueue;

    @Value("${order.accounting.price.exchange}")
    private String priceExchange;

    @PostConstruct
    private void postConstruct() {
        unprocessableMessageHandler = new UnprocessableMessageHandler(rejectedOrderExchange);
    }

    @Override
    public OrderRecordDto preprocessOrderRequest(OrderRequestDto orderDto) {
        Order order = orderService.createOrderRequest(orderDto);

        OrderRecordDto orderRecordDto = orderMapper.orderToOrderRecordDto(order);
        rabbitTemplate.convertAndSend(orderValidationExchange, "", orderRecordDto);
        return orderRecordDto;
    }

    @RabbitListener(queues = "${order.validation.response.queue}", containerFactory = "customRabbitListener")
    @Override
    public void validateOrder(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        ValidationResultDto resultDto = objectMapper.readValue(message.getBody(), ValidationResultDto.class);
        log.info("Consuming validation result : {}", resultDto);

        // check if any validation record exists in cache
        ValidationResult result = Optional.ofNullable(validationCacheTemplate.opsForValue()
                        .get(getOrderCacheKey(resultDto.getOrderId())))
                .orElse(new ValidationResult(resultDto.getOrderId()));

        if (result.isRejected()) {
            // check whether the order is already rejected from cache
            log.info("Order is already rejected, orderId : {}", resultDto.getOrderId());
            channel.basicAck(deliveryTag, false);
        } else if (isRejected(result)) {
            rejectOrder(channel, deliveryTag, result);
        } else if (isReadyToProceed(result) && !result.isProceeded()) {
            acceptOrder(channel, deliveryTag, result);
        } else {
            // map new results to ValidationResult from dto
            updateResults(resultDto, result);

            retryLater(message, channel, deliveryTag, result);
        }
    }

    @Override
    @RabbitListener(queues = "${order.status.queue}")
    public void validateOrder(StatusUpdateDto updateDto) {
        orderService.updateOrderStatus(updateDto.getOrderId(), updateDto.getStatus());
    }

    private void retryLater(Message message, Channel channel, long deliveryTag, ValidationResult result) throws IOException {
        if (!isReadyToProceed(result) && !result.isRejected()) {
            validationCacheTemplate.opsForValue().set(getOrderCacheKey(result.getOrderId()), result, Duration.ofMinutes(1));
            log.info("Cached validation result : {}", result);

            unprocessableMessageHandler.handleErrorProcessingMessage(message, channel, deliveryTag);
        } else if (result.isRejected()) {
            // check whether the order is already rejected from cache
            log.info("Order is already rejected, orderId : {}", result.getOrderId());
            channel.basicAck(deliveryTag, false);
        } else {
            log.info("Ready to proceed, it will be proceeded as soon, orderId : {}", result.getOrderId());
            validationCacheTemplate.opsForValue().set(getOrderCacheKey(result.getOrderId()), result, Duration.ofMinutes(1));
            channel.basicAck(deliveryTag, false);
        }
    }

    private void acceptOrder(Channel channel, long deliveryTag, ValidationResult result) throws IOException {
        // ready to be proceeded
        log.info("Validated the order, orderId : {}", result.getOrderId());

        orderService.updateOrderStatus(result.getOrderId(), OrderStatus.VALIDATED);

        // sent it to price queue in order to determine prices
        OrderRecordDto order = orderService.getOrder(result.getOrderId());
        rabbitTemplate.convertAndSend(priceExchange, priceQueue, order);
        log.info("Proceeded it to calculate total cost, orderId : {}", result.getOrderId());

        // set isProceeded = true and cache it, in case other validation message
        result.setProceeded(true);
        validationCacheTemplate.opsForValue().set(getOrderCacheKey(result.getOrderId()), result, Duration.ofMinutes(1));
        channel.basicAck(deliveryTag, false);
    }

    private void rejectOrder(Channel channel, long deliveryTag, ValidationResult result) throws IOException {
        // check if order will be rejected
        log.info("Order is rejected, orderId : [{}]", result);
        RejectedOrder rejectedOrder = new RejectedOrder(result.getOrderId(), getRejectionReason(result));
        rabbitTemplate.convertAndSend(rejectedOrderExchange, "", rejectedOrder);

        // set isRejected = true and cache it, in case other validation message
        result.setRejected(true);
        validationCacheTemplate.opsForValue().set(getOrderCacheKey(result.getOrderId()), result, Duration.ofMinutes(1));
        channel.basicAck(deliveryTag, false);
    }

    private String getOrderCacheKey(UUID orderId) {
        return CacheUtil.getCacheKey(CacheUtil.VALIDATION_CACHE_ORDER_KEY, orderId);
    }

    private void updateResults(ValidationResultDto validationResultDto, ValidationResult result) {
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

