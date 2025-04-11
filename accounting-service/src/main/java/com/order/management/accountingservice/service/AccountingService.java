package com.order.management.accountingservice.service;

import com.order.management.accountingservice.dto.OrderRecordDto;

public interface AccountingService {
    void calculateCost(OrderRecordDto orderRecordDto);
}

