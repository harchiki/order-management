package com.order.management.orderservice.mapper;

import com.order.management.orderservice.dto.order.OrderRecordDto;
import com.order.management.orderservice.dto.order.OrderRequestDto;
import com.order.management.orderservice.dto.product.ProductRequestDto;
import com.order.management.orderservice.model.ProductRequest;
import com.order.management.orderservice.model.Order;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "orderId", expression = "java(order.getId())")
    OrderRecordDto orderToOrderRecordDto(Order order);

    @Mapping(target = "cart", expression = "java(productRequestDtoToRequestItem(order, orderRequestDto))")
    void orderRequestDtoToOrder(@MappingTarget Order order, OrderRequestDto orderRequestDto);

    default Set<ProductRequest> productRequestDtoToRequestItem(Order order, OrderRequestDto orderRequestDto) {
        return orderRequestDto.getCart().stream()
                .map(this::orderRequestDtoToOrder)
                .peek(item -> item.setOrder(order))
                .collect(Collectors.toSet());
    }

    @Mapping(target = "order", ignore = true)
    ProductRequest orderRequestDtoToOrder(ProductRequestDto orderRequestDto);
    ProductRequestDto productRequestToProductRequestDto(ProductRequest order);
    Set<ProductRequestDto> productRequestToProductRequestDto(Set<ProductRequest> order);
}

