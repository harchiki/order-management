package com.order.management.stockservice.dto;

import com.order.management.stockservice.constant.OrderStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class OrderMessage {
    private UUID orderId;
    private Set<ProductRequestDto> cart;
    private OrderStatus status;
}
