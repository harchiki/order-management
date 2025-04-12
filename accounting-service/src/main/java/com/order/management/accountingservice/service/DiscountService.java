package com.order.management.accountingservice.service;

import com.order.management.accountingservice.service.external.dto.DiscountResponseDto;
import com.order.management.accountingservice.service.external.dto.ProductPriceDto;

import java.util.List;

public interface DiscountService {
    DiscountResponseDto getDiscountDetail(String discountCode);
}
