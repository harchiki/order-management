package com.order.management.orderservice.dto.order;

import com.order.management.common.constant.OrderStatus;
import com.order.management.common.constant.PaymentType;
import com.order.management.orderservice.dto.product.ProductRequestDto;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class OrderRecordDto {
    private UUID orderId;
    private Set<ProductRequestDto> cart;
    private OrderStatus status;
    private String discountCode;
    private PaymentType paymentType;
}
