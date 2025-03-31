package com.order.management.orderservice.dto.order;

import com.order.management.orderservice.constant.OrderStatus;
import com.order.management.orderservice.dto.product.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class OrderMessage {
    private UUID orderId;
    private Set<Product> cart;
    private OrderStatus status;
    private String discountCode;

    public OrderMessage() {
        this.orderId = UUID.randomUUID();
    }
}
