package com.order.management.paymentservice.service.impl;

import com.order.management.paymentservice.dto.PaymentRequest;
import com.order.management.paymentservice.service.PaymentProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentProviderImpl implements PaymentProvider {

    @Override
    @RabbitListener(queues = "${order.payment.credit-card}")
    public void provideCreditCardPayment(PaymentRequest paymentRequest) {
        log.info("Payment succeeds via credit card, order : [{}]", paymentRequest);
    }

    @Override
    @RabbitListener(queues = "${order.payment.wallet}")
    public void provideDigitalWalletPayment(PaymentRequest paymentRequest) {
        log.info("Payment succeeds via digital wallet, order : [{}]", paymentRequest);

    }

    @Override
    @RabbitListener(queues = "${order.payment.bank-transfer}")
    public void provideBankTransferPayment(PaymentRequest paymentRequest) {
        log.info("Payment succeeds via bank-transfer, order : [{}]", paymentRequest);

    }
}
