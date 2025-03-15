package com.order.management.orderservice.mapper;

import com.order.management.orderservice.dto.order.Order;
import com.order.management.orderservice.dto.order.OrderRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order orderRequestDtoToOrder(OrderRequestDto orderRequestDto);
}
