package com.order.management.discountservice.dto;

import com.order.management.common.constant.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
public class OrderMessage {
    private UUID orderId;
    private OrderStatus status;
    private String discountCode;
}
