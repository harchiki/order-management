package com.order.management.stockservice.repository;

import com.order.management.stockservice.model.Product;
import com.order.management.stockservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Set<Reservation> findByOrderId(UUID orderId);
}
