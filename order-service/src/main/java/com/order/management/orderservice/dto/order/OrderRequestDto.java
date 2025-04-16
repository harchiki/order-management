package com.order.management.orderservice.dto.order;

import com.order.management.common.constant.PaymentType;
import com.order.management.orderservice.dto.product.ProductRequestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class OrderRequestDto {
    private Set<ProductRequestDto> cart;
    private String discountCode;
    private PaymentType paymentType;
}
