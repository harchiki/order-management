package com.order.management.productservice.service.impl;

import com.order.management.common.constant.ValidationStatus;
import com.order.management.productservice.dto.OrderRequestDto;
import com.order.management.productservice.dto.ProductRequest;
import com.order.management.productservice.dto.RejectedOrder;
import com.order.management.productservice.dto.StockValidationResult;
import com.order.management.productservice.model.Product;
import com.order.management.productservice.model.Reservation;
import com.order.management.productservice.service.ProductService;
import com.order.management.productservice.service.ReservationService;
import com.order.management.productservice.service.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StockServiceImpl implements StockService {
    private final ProductService productService;
    private final ReservationService reservationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${order.validation.response.exchange}")
    private String validationResponseExchange;

    @Override
    @RabbitListener(queues = "${order.validation.stock.queue}")
    public void consumeOrder(OrderRequestDto order) {
        log.info("Consuming OrderMessage : [{}]", order);
        boolean isAllStockEnough = order.getCart().stream().allMatch(this::isStockEnough);
        StockValidationResult result = new StockValidationResult();
        result.setOrderId(order.getOrderId());

        ValidationStatus status;
        if (isAllStockEnough) {
            // reserve items in stock
            reservationService.reserve(order);
            status = ValidationStatus.OKAY;
        } else {
            status = ValidationStatus.REJECTED;
        }

        result.setStockStatus(status);
        rabbitTemplate.convertAndSend(validationResponseExchange, "", result);
    }

    //todo make rejected queue as stream
    @RabbitListener(queues = "${order.rejected.queue}")
    @Transactional
    @Override
    public void backToStock(RejectedOrder order) {
        log.info("Consuming Rejected Order, orderId : {}", order.getOrderId());
        Set<Reservation> reservations = reservationService.findByOrderId(order.getOrderId());
        reservations.stream()
                .map(reservation -> {
                    ProductRequest request = new ProductRequest();
                    request.setProductId(reservation.getProduct().getProductId());
                    request.setQuantity(reservation.getQuantity());
                    return request;
                })
                .forEach(productService::increaseStock);
        reservationService.updateReservationsBackToStock(order.getOrderId());
    }

    private boolean isStockEnough(ProductRequest requestDto) {
        Optional<Product> optProduct = productService.findById(requestDto.getProductId());
        if (optProduct.isEmpty()) {
            return false;
        }
        Product product = optProduct.get();
        return requestDto.getQuantity() <= product.getQuantity();
    }
}
