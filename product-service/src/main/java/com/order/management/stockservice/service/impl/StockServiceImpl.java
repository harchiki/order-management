package com.order.management.stockservice.service.impl;

import com.order.management.common.constant.ValidationStatus;
import com.order.management.stockservice.dto.OrderRequestDto;
import com.order.management.stockservice.dto.ProductRequest;
import com.order.management.stockservice.dto.StockValidationResult;
import com.order.management.stockservice.model.Product;
import com.order.management.stockservice.service.ProductService;
import com.order.management.stockservice.service.ReservationService;
import com.order.management.stockservice.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
            reservationService.reserve(order);
            status = ValidationStatus.OKAY;
        } else {
            status = ValidationStatus.REJECTED;
        }

        result.setStockStatus(status);
        rabbitTemplate.convertAndSend(validationResponseExchange, "", result);
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
