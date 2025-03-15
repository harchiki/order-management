package com.order.management.orderservice.dto.order;

import com.order.management.orderservice.data.OrderStatus;
import com.order.management.orderservice.dto.product.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class Order {
    private UUID orderId;
    private Set<Product> cart;
    private OrderStatus status;

    public Order() {
        this.orderId = UUID.randomUUID();
    }
}
