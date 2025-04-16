package com.order.management.accountingservice.service.impl;

import com.order.management.accountingservice.dto.OrderPrice;
import com.order.management.accountingservice.service.AccountingManagement;

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

    @Value("${order.payment.exchange}")
    private String paymentExchange;

    @Override
    public void sendToPayment(OrderPrice orderPrice) {
        log.info("Order sending to payment, orderId : {}, paymentType : {}",
                orderPrice.getOrderId(), orderPrice.getPaymentType().getName());
        String routingKey = orderPrice.getPaymentType().getKey();
        rabbitTemplate.convertAndSend(paymentExchange, routingKey, orderPrice);
    }
}
