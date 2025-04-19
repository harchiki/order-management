package com.order.management.accountingservice.mapper;

import com.order.management.accountingservice.dto.OrderPrice;
import com.order.management.accountingservice.dto.OrderRecordDto;
import com.order.management.accountingservice.dto.PaymentRequest;
import com.order.management.accountingservice.dto.StatusUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderPriceMapper {
    @Mapping(target = "cart", ignore = true)
    OrderPrice orderRecordDtoToOrderPriceDto(OrderRecordDto orderRecordDto);
    PaymentRequest orderPriceToPaymentRequest(OrderPrice order);
    StatusUpdateDto orderPriceToStatusUpdateDto(OrderPrice order);
}
