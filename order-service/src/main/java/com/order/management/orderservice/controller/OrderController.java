package com.order.management.orderservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.order.management.orderservice.dto.order.OrderRecordDto;
import com.order.management.orderservice.dto.order.OrderRequestDto;
import com.order.management.orderservice.service.OrderManagementService;
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
    private final OrderManagementService orderManagementService;

    @PostMapping("/payment")
    public ResponseEntity<OrderRecordDto> paymentProcess(@RequestBody OrderRequestDto orderRequestDto) throws JsonProcessingException {
        OrderRecordDto orderRecordDto = orderManagementService.preprocessOrderRequest(orderRequestDto);
        return ResponseEntity.ok(orderRecordDto);
    }
}
