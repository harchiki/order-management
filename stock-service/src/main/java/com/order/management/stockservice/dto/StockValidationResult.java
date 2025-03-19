package com.order.management.stockservice.dto;

import com.order.management.stockservice.constant.ValidationStatus;
import com.order.management.stockservice.constant.ValidationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class StockValidationResult {
    private UUID orderId;
    private ValidationType validationType;
    private ValidationStatus status;
}
