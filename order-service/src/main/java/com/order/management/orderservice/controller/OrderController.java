package com.order.management.orderservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.order.management.orderservice.dto.order.OrderRequestDto;
import com.order.management.orderservice.dto.order.Order;
import com.order.management.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/payment")
    public ResponseEntity<Order> paymentProcess(@RequestBody OrderRequestDto orderRequestDto) throws JsonProcessingException {
        Order order = orderService.publishPayment(orderRequestDto);
        return ResponseEntity.ok(order);
    }
}
