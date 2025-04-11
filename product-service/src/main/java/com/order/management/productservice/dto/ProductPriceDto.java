package com.order.management.productservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductPriceDto {
    private Long productId;
    private double price;
}
