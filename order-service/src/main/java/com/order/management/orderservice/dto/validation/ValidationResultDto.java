package com.order.management.orderservice.dto.validation;

import com.order.management.common.constant.ValidationStatus;
import com.order.management.common.constant.ValidationType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Data
@NoArgsConstructor
@Getter
@Setter
public class ValidationResultDto {
    private UUID orderId;
    private ValidationType validationType;
    private ValidationStatus status;
}
