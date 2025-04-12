package com.order.management.discountservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DiscountResponseDto {
    private String discountCode;
    double discountAmount;
}
