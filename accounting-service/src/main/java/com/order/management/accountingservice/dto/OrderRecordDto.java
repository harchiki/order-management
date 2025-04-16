package com.order.management.accountingservice.dto;

import com.order.management.common.constant.OrderStatus;

import com.order.management.common.constant.PaymentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class OrderRecordDto {
    private UUID orderId;
    private List<ProductRequestDto> cart;
    private OrderStatus status;
    private String discountCode;
    private PaymentType paymentType;
}
