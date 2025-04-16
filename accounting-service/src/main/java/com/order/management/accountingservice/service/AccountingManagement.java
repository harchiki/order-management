package com.order.management.accountingservice.service;

import com.order.management.accountingservice.dto.OrderPrice;

public interface AccountingManagement {
    void sendToPayment(OrderPrice orderPrice);
}
