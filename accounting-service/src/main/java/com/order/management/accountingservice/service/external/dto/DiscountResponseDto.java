package com.order.management.accountingservice.service.external.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DiscountResponseDto {
    private String discountCode;
    double discountAmount;
}
