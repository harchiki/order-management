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
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

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
    private final RedisTemplate<String, RejectedOrder> rejectedOrderCacheTemplate;

    @Value("${order.validation.response.dlx}")
    private String rejectedOrderExchange;

    @Value("${order.validation.exchange}")
    private String orderValidationExchange;

    @Override
    public OrderMessage validateOrder(OrderRequestDto orderDto) throws JsonProcessingException {
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
        UUID orderId = validationResultDto.getOrderId();
        final String validationCacheKey = CacheUtil.getCacheKey(CacheUtil.VALIDATION_CACHE_ORDER_KEY, orderId);
        // check whether the order is already rejected from cache
        boolean isAlreadyRejected = isAlreadyRejected(orderId);

        if (isAlreadyRejected) {
            log.debug("Order is already rejected, orderId : {}", orderId);
            channel.basicAck(deliveryTag, false);
            return;
        }

        // check if any validation record exists in cache
        ValidationResult result = Optional.ofNullable(validationCacheTemplate.opsForValue().get(validationCacheKey))
                .orElse(new ValidationResult(orderId));

        // map stock status if exists
        Optional.ofNullable(validationResultDto.getStockStatus())
                .ifPresent(result::setStockStatus);

        // map discount status if exists
        Optional.ofNullable(validationResultDto.getDiscountStatus())
                .ifPresent(result::setDiscountStatus);

        // check results if any rejected
        if (isRejected(result)) {
            log.info("Order is just rejected, orderId : [{}]", result);
            RejectedOrder rejectedOrder = new RejectedOrder(orderId, getRejectionReason(result));
            rejectedOrderCacheTemplate.opsForValue()
                    .set(CacheUtil.getCacheKey(CacheUtil.REJECTED_ORDER_CACHE_KEY, orderId), rejectedOrder, Duration.ofMinutes(1));
            channel.basicAck(deliveryTag, false);
            rabbitTemplate.convertAndSend(rejectedOrderExchange, "",rejectedOrder);
            return;
        }

        if (Optional.ofNullable(result.getDiscountStatus()).isEmpty()
                || Optional.ofNullable(result.getStockStatus()).isEmpty()) {
            validationCacheTemplate.opsForValue().set(validationCacheKey, result, Duration.ofMinutes(1));
            log.info("Cached validation result : {}", result);

            log.info("Will be retried later, orderId : {}", orderId);
            UnprocessableMessageHandler unprocessableMessageHandler = getUnprocessableMessageHandler(rejectedOrderExchange);
            unprocessableMessageHandler.handleErrorProcessingMessage(message, channel, deliveryTag);
            return;
        }

        if (isEverythingOkay(result)) {
            channel.basicAck(deliveryTag, false);
            log.info("Validated the order, orderId : {}", result.getOrderId());
            // todo redirect it to calculate cost
            log.info("Proceed it to calculate total cost");
            // rabbitTemplate.convertAndSend();

            validationCacheTemplate.delete(validationCacheKey);
            log.info("Removed order from validation cache, orderId : {}", result.getOrderId());
        }
    }

    private boolean isAlreadyRejected(UUID orderId) {
        return Optional.ofNullable(rejectedOrderCacheTemplate.opsForValue()
                        .get(CacheUtil.getCacheKey(CacheUtil.REJECTED_ORDER_CACHE_KEY, orderId))).isPresent();
    }

    private boolean isRejected(ValidationResult result) {
        List<ValidationStatus> rejects = Stream.of(result.getDiscountStatus(), result.getStockStatus())
                .filter(ValidationStatus.REJECTED::equals)
                .toList();
        return CollectionUtils.isNotEmpty(rejects);
    }

    private boolean isEverythingOkay(ValidationResult result) {
        return ValidationStatus.OKAY.equals(result.getStockStatus())
                && ValidationStatus.OKAY.equals(result.getDiscountStatus());
    }

    private String getRejectionReason(ValidationResult result) {
        if (ValidationStatus.REJECTED.equals(result.getDiscountStatus())) return ValidationType.DISCOUNT_CODE.name();
        else if (ValidationStatus.REJECTED.equals(result.getStockStatus())) return  ValidationType.STOCK.name();
        else return "Unknown Reason";
    }

    private UnprocessableMessageHandler getUnprocessableMessageHandler(String rejectedOrderExchange) {
        return new UnprocessableMessageHandler(rejectedOrderExchange);
    }
}

