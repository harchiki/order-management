package com.order.management.accountingservice.dto;

import com.order.management.common.constant.OrderStatus;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class OrderPrice {
    private UUID orderId;
    private List<ProductTotalPriceDto> cart;
    private double totalRawPrice;
    private double totalPrice;
    private OrderStatus status;
    private String discountCode;
    private double discount;
}
