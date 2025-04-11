package com.order.management.productservice.service;

import com.order.management.productservice.dto.OrderRequestDto;
import com.order.management.productservice.model.Reservation;

import java.util.Set;
import java.util.UUID;

public interface ReservationService {
    void reserve(OrderRequestDto order);
    Set<Reservation> findByOrderId(UUID orderId);
    void updateReservationsBackToStock(UUID orderId);
}
