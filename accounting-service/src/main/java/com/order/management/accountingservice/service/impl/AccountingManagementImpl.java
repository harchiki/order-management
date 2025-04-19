package com.order.management.accountingservice.service.impl;

import com.order.management.accountingservice.dto.OrderPrice;
import com.order.management.accountingservice.dto.PaymentRequest;
import com.order.management.accountingservice.mapper.OrderPriceMapper;
import com.order.management.accountingservice.service.AccountingManagement;

import com.order.management.accountingservice.dto.StatusUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountingManagementImpl implements AccountingManagement {
    private final RabbitTemplate rabbitTemplate;
    private final OrderPriceMapper mapper;

    @Value("${order.payment.exchange}")
    private String paymentExchange;

    @Value("${order.status.exchange}")
    private String statusExchange;

    @Value("${order.status.key}")
    private String statusKey;

    @Override
    public void sendToPayment(OrderPrice orderPrice) {
        log.info("Order sending to payment, orderId : {}, paymentType : {}",
                orderPrice.getOrderId(), orderPrice.getPaymentType().getName());
        String routingKey = orderPrice.getPaymentType().getKey();

        updateOrderStatus(orderPrice);

        PaymentRequest paymentRequest = mapper.orderPriceToPaymentRequest(orderPrice);
        rabbitTemplate.convertAndSend(paymentExchange, routingKey, paymentRequest);
    }

    public void updateOrderStatus(OrderPrice orderPrice) {
        StatusUpdateDto updateDto = mapper.orderPriceToStatusUpdateDto(orderPrice);

        log.info("Order status is updated, orderId : {}, status : {}", updateDto.getOrderId(), updateDto.getStatus());
        rabbitTemplate.convertAndSend(statusExchange, statusKey, updateDto);
    }
}
