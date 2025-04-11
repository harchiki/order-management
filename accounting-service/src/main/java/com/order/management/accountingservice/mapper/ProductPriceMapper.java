package com.order.management.accountingservice.mapper;

import com.order.management.accountingservice.dto.ProductRequestDto;
import com.order.management.accountingservice.dto.ProductTotalPriceDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductPriceMapper {
    List<ProductTotalPriceDto> productRequestDtoToProductTotalPriceDto(List<ProductRequestDto> requestDtoList);
}
