package com.order.management.accountingservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProductTotalPriceDto {
    private Long productId;
    private double price;
    private long quantity;
    private double totalPrice;
}
