package com.order.management.stockservice.service;

import com.order.management.stockservice.dto.OrderMessage;
import com.order.management.stockservice.dto.ProductRequestDto;
import com.order.management.stockservice.model.Product;
import com.order.management.stockservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.order.management.stockservice.constant.RabbitMQConstant.*;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StockValidationServiceImpl implements StockValidationService {
    private final ProductRepository productRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @RabbitListener(queues = STOCK_VALIDATION_QUEUE)
    public void consumeOrder(OrderMessage order) {
        boolean isAllStockEnough = order.getCart().stream().allMatch(this::isStockEnough);
        if (isAllStockEnough) {
//            verifyStock(order);
        }
//        rejectOrder(order);
    }

    @Override
    public void rejectOrder(OrderMessage order) {
        rabbitTemplate.convertAndSend(REJECTED_ORDER_DLX, "", order);
    }

    @Override
    public void verifyStock(OrderMessage order) {
        rabbitTemplate.convertAndSend(VALID_ORDER_EXCHANGE, "", order);
    }

    private boolean isStockEnough(ProductRequestDto requestDto) {
        Optional<Product> optProduct = productRepository.findById(requestDto.getProductId());
        if (optProduct.isEmpty()) {
            return false;
        }
        Product product = optProduct.get();
        return requestDto.getQuantity() <= product.getQuantity();
    }
}
