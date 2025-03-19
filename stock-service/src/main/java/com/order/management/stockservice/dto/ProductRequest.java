package com.order.management.stockservice.dto;

import com.order.management.stockservice.constant.ValidationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class ProductRequest {
    private Long productId;
    private String name;
    private long quantity;
}
