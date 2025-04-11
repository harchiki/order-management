package com.order.management.productservice.dto;

import com.order.management.common.constant.ValidationStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class StockValidationResult {
    private UUID orderId;
    private ValidationStatus stockStatus;
}
