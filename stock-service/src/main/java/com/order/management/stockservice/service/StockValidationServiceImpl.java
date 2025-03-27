package com.order.management.stockservice.service;

import com.order.management.stockservice.constant.ValidationStatus;
import com.order.management.stockservice.constant.ValidationType;
import com.order.management.stockservice.dto.OrderMessage;
import com.order.management.stockservice.dto.ProductRequest;
import com.order.management.stockservice.dto.StockValidationResult;
import com.order.management.stockservice.model.Product;
import com.order.management.stockservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StockValidationServiceImpl implements StockValidationService {
    private final ProductRepository productRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${order.validation.response.exchange}")
    private String validationResponseExchange;

    @Override
    @RabbitListener(queues = "${order.validation.stock.queue}")
    public void consumeOrder(OrderMessage order) {
        boolean isAllStockEnough = order.getCart().stream().allMatch(this::isStockEnough);
        StockValidationResult result = new StockValidationResult();
        result.setOrderId(order.getOrderId());
        result.setValidationType(ValidationType.STOCK);
        ValidationStatus status = isAllStockEnough ? ValidationStatus.OKAY : ValidationStatus.REJECTED;
        result.setStatus(status);

        rabbitTemplate.convertAndSend(validationResponseExchange, "", result);
    }

    private boolean isStockEnough(ProductRequest requestDto) {
        Optional<Product> optProduct = productRepository.findById(requestDto.getProductId());
        if (optProduct.isEmpty()) {
            return false;
        }
        Product product = optProduct.get();
        return requestDto.getQuantity() <= product.getQuantity();
    }
}
