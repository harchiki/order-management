package com.order.management.productservice.mapper;

import com.order.management.productservice.dto.ProductPriceDto;
import com.order.management.productservice.model.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    List<ProductPriceDto> productToProductPriceDto(List<Product> productList);
}
