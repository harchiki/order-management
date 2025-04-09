package com.order.management.stockservice.service;

import com.order.management.stockservice.dto.OrderRequestDto;

public interface ReservationService {
    void reserve(OrderRequestDto order);
}
