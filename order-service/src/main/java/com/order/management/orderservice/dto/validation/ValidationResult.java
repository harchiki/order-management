package com.order.management.orderservice.dto.validation;

import com.order.management.common.constant.ValidationStatus;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Data
@NoArgsConstructor
@Getter
@Setter
public class ValidationResult {
    private UUID orderId;
    private ValidationStatus stockStatus;
    private ValidationStatus discountStatus;
    private boolean isProceeded;
    private boolean isRejected;

    public ValidationResult(UUID orderId) {
        this.orderId = orderId;
    }
}
