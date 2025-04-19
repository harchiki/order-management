package com.order.management.paymentservice.service;

import com.order.management.paymentservice.dto.PaymentRequest;

public interface PaymentProvider {
    void provideCreditCardPayment(PaymentRequest paymentRequest);
    void provideDigitalWalletPayment(PaymentRequest paymentRequest);
    void provideBankTransferPayment(PaymentRequest paymentRequest);
}
