package com.order.management.orderservice.dto.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class ProductRequestDto {
    private Long productId;
    private long quantity;
}
