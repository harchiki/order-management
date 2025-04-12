package com.order.management.accountingservice.service.impl;

import com.order.management.accountingservice.service.DiscountService;
import com.order.management.accountingservice.service.external.DiscountClient;
import com.order.management.accountingservice.service.external.dto.DiscountResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DiscountServiceImpl implements DiscountService {
    private final DiscountClient discountClient;

    @Override
    public DiscountResponseDto getDiscountDetail(String discountCode) {
        ResponseEntity<DiscountResponseDto> response = discountClient.getDiscountDetail(discountCode);
        return response.getBody();
    }
}
