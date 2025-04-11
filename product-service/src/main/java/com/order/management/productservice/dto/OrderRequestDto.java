package com.order.management.productservice.dto;

import com.order.management.common.constant.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
public class OrderRequestDto {
    private UUID orderId;
    private Set<ProductRequest> cart;
    private OrderStatus status;
}
