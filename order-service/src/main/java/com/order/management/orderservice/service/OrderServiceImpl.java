package com.order.management.orderservice.service;

import com.order.management.common.constant.OrderStatus;
import com.order.management.common.constant.ValidationStatus;
import com.order.management.common.constant.ValidationType;
import com.order.management.orderservice.dto.order.OrderMessage;
import com.order.management.orderservice.dto.order.OrderRequestDto;
import com.order.management.orderservice.dto.validation.RejectedOrder;
import com.order.management.orderservice.dto.validation.ValidationResult;
import com.order.management.orderservice.dto.validation.ValidationResultDto;
import com.order.management.orderservice.mapper.OrderMapper;

import com.order.management.orderservice.model.Order;
import com.order.management.orderservice.repository.OrderRepository;
import com.order.management.orderservice.util.CacheUtil;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    // key order id
    private final RedisTemplate<String, ValidationResult> validationCacheTemplate;
    private final RedisTemplate<String, RejectedOrder> rejectedOrderCacheTemplate;


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
        final String validationCacheKey = CacheUtil.getCacheKey(CacheUtil.VALIDATION_CACHE_ORDER_KEY, orderId);
        // check whether the order is already rejected from cache
        boolean isAlreadyRejected = isAlreadyRejected(orderId);

        if (isAlreadyRejected) {
            log.debug("Order is already rejected, orderId : {}", orderId);
            // todo throw exception without retry
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
        if (isAnyRejected(result)) {
            log.info("Order is just rejected, orderId : [{}]", result);
            RejectedOrder rejectedOrder = new RejectedOrder(orderId, getRejectionReason(result));
            rejectedOrderCacheTemplate.opsForValue()
                    .set(CacheUtil.getCacheKey(CacheUtil.REJECTED_ORDER_CACHE_KEY, orderId), rejectedOrder, Duration.ofMinutes(1));
            // todo throw exception and send it rejected order queue
        }

        if (Optional.ofNullable(result.getDiscountStatus()).isEmpty()
                || Optional.ofNullable(result.getStockStatus()).isEmpty()) {
            validationCacheTemplate.opsForValue().set(validationCacheKey, result, Duration.ofMinutes(1));
            log.info("Cached validation result : {}", result);
            // todo throw exception and send it to waiting and retry later
        }

        if (isEverythingOkay(result)) {
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

    private boolean isAnyRejected(ValidationResult result) {
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
}

