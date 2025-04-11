package com.order.management.productservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProductRequest {
    private Long productId;
    private String name;
    private long quantity;
}
