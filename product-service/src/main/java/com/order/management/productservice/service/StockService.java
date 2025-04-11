package com.order.management.productservice.service;

import com.order.management.productservice.dto.OrderRequestDto;
import com.order.management.productservice.dto.RejectedOrder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public interface StockService {
    void consumeOrder(OrderRequestDto order);

    @RabbitListener(queues = "${order.rejected.queue}")
    void backToStock(RejectedOrder order);
}
