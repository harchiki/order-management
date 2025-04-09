package com.order.management.stockservice.service.impl;

import com.order.management.stockservice.constant.ReservationStatus;
import com.order.management.stockservice.dto.OrderRequestDto;
import com.order.management.stockservice.dto.ProductRequest;
import com.order.management.stockservice.model.Product;
import com.order.management.stockservice.model.Reservation;
import com.order.management.stockservice.repository.ReservationRepository;
import com.order.management.stockservice.service.ProductService;
import com.order.management.stockservice.service.ReservationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class ReservationServiceImpl implements ReservationService {
    private final ProductService productService;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public void reserve(OrderRequestDto order) {
        Set<Reservation> reservedSet = order.getCart().stream()
                .map(request -> reserve(order.getOrderId(), request))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        reservationRepository.saveAll(reservedSet);
    }

    @Override
    public Set<Reservation> findByOrderId(UUID orderId) {
        return reservationRepository.findByOrderId(orderId);
    }

    @Override
    public void updateReservationsBackToStock(UUID orderId) {
        Set<Reservation> updatedReservations = reservationRepository.findByOrderId(orderId)
                .stream()
                .peek(reservation -> reservation.setStatus(ReservationStatus.BACK_TO_STOCK))
                .collect(Collectors.toSet());
        reservationRepository.saveAll(updatedReservations);
    }

    private Optional<Reservation> reserve(UUID orderId, ProductRequest request) {
        Optional<Product> optProduct = productService.findById(request.getProductId());
        if (optProduct.isPresent()) {
            Reservation reservation = new Reservation();
            reservation.setOrderId(orderId);
            reservation.setProduct(optProduct.get());
            reservation.setQuantity(request.getQuantity());
            reservation.setStatus(ReservationStatus.RESERVED);
            productService.decreaseStock(request);
            return Optional.of(reservation);
        }
        return Optional.empty();
    }
}
