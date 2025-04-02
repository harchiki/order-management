package com.order.management.orderservice.mapper;

import com.order.management.orderservice.dto.order.OrderMessage;
import com.order.management.orderservice.dto.order.OrderRequestDto;
import com.order.management.orderservice.dto.product.ProductRequestDto;
import com.order.management.orderservice.model.RequestItem;
import com.order.management.orderservice.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "orderId", expression = "java(order.getId())")
    OrderMessage orderToOrderMessage(Order order);

    @Mapping(target = "cart", expression = "java(productRequestDtoToRequestItem(order, orderRequestDto))")
    void orderRequestDtoToOrder(@MappingTarget Order order, OrderRequestDto orderRequestDto);

    default Set<RequestItem> productRequestDtoToRequestItem(Order order, OrderRequestDto orderRequestDto) {
        return orderRequestDto.getCart().stream()
                .map(this::orderRequestDtoToOrder)
                .peek(item -> item.setOrder(order))
                .collect(Collectors.toSet());
    }

    @Mapping(target = "order", ignore = true)
    RequestItem orderRequestDtoToOrder(ProductRequestDto orderRequestDto);
    ProductRequestDto orderToOrderMessage(RequestItem order);
}

