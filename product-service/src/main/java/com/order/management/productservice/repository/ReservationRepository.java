package com.order.management.productservice.repository;

import com.order.management.productservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Set<Reservation> findByOrderId(UUID orderId);
}
