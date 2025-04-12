package com.order.management.accountingservice.service.external.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProductPriceDto {
    private Long productId;
    private double price;
}
