package com.order.management.accountingservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProductTotalPriceDto {
    private Long productId;
    private double price;
    private long quantity;
    private double totalPrice;
}
