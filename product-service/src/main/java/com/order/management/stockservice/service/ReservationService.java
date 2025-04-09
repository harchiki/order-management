package com.order.management.stockservice.service;

import com.order.management.stockservice.dto.OrderRequestDto;
import com.order.management.stockservice.model.Reservation;

import java.util.Set;
import java.util.UUID;

public interface ReservationService {
    void reserve(OrderRequestDto order);
    Set<Reservation> findByOrderId(UUID orderId);
    void updateReservationsBackToStock(UUID orderId);
}
