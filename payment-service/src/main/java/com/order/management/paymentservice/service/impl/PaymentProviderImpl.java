package com.order.management.paymentservice.service.impl;

import com.order.management.common.constant.OrderStatus;
import com.order.management.paymentservice.dto.PaymentRequest;
import com.order.management.paymentservice.dto.StatusUpdateDto;
import com.order.management.paymentservice.service.PaymentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentProviderImpl implements PaymentProvider {
    private final RabbitTemplate rabbitTemplate;

    @Value("${order.status.exchange}")
    private String statusExchange;

    @Value("${order.status.key}")
    private String statusKey;

    @Override
    @RabbitListener(queues = "${order.payment.credit-card.queue}")
    public void provideCreditCardPayment(PaymentRequest paymentRequest) {
        log.info("Payment succeeds via credit card, order : [{}]", paymentRequest);
        updateOrderStatus(paymentRequest.getOrderId());
    }

    @Override
    @RabbitListener(queues = "${order.payment.wallet.queue}")
    public void provideDigitalWalletPayment(PaymentRequest paymentRequest) {
        log.info("Payment succeeds via digital wallet, order : [{}]", paymentRequest);
        updateOrderStatus(paymentRequest.getOrderId());
    }

    @Override
    @RabbitListener(queues = "${order.payment.bank-transfer.queue}")
    public void provideBankTransferPayment(PaymentRequest paymentRequest) {
        log.info("Payment succeeds via bank-transfer, order : [{}]", paymentRequest);
        updateOrderStatus(paymentRequest.getOrderId());
    }

    private void updateOrderStatus(UUID orderId) {
        StatusUpdateDto updateDto = new StatusUpdateDto();
        updateDto.setOrderId(orderId);
        updateDto.setStatus(OrderStatus.PAID);

        log.info("Order status is updated, orderId : {}, status : {}", updateDto.getOrderId(), updateDto.getStatus());
        rabbitTemplate.convertAndSend(statusExchange, statusKey, updateDto);
    }
}
