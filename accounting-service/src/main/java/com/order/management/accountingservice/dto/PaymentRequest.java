package com.order.management.accountingservice.dto;

import com.order.management.common.constant.PaymentType;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
public class PaymentRequest {
    private UUID orderId;
    private double totalPrice;
    private PaymentType paymentType;
}
