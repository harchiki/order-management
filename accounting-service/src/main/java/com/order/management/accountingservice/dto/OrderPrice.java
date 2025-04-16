package com.order.management.accountingservice.dto;

import com.order.management.common.constant.OrderStatus;
import com.order.management.common.constant.PaymentType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Data
public class OrderPrice {
    private UUID orderId;
    private List<ProductTotalPriceDto> cart;
    private double totalRawPrice;
    private double totalPrice;
    private OrderStatus status;
    private String discountCode;
    private double discount;
    private PaymentType paymentType;
}
