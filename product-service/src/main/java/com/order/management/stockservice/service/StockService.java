package com.order.management.stockservice.service;

import com.order.management.stockservice.dto.OrderRequestDto;
import com.order.management.stockservice.dto.RejectedOrder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public interface StockService {
    void consumeOrder(OrderRequestDto order);

    @RabbitListener(queues = "${order.rejected.queue}")
    void backToStock(RejectedOrder order);
}
