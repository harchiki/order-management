package com.order.management.orderservice.dto.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class Product {
    private UUID productId;
    private String name;
    private long quantity;
}
