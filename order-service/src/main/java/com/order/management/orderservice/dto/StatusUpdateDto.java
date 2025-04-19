package com.order.management.orderservice.dto;

import com.order.management.common.constant.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class StatusUpdateDto {
    private UUID orderId;
    private OrderStatus status;
}
