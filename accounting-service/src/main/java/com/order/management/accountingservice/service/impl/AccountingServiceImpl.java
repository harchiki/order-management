package com.order.management.accountingservice.service.impl;

import com.order.management.accountingservice.dto.OrderPrice;
import com.order.management.accountingservice.dto.OrderRecordDto;
import com.order.management.accountingservice.service.AccountingManagement;
import com.order.management.accountingservice.service.DiscountService;
import com.order.management.accountingservice.service.external.DiscountClient;
import com.order.management.accountingservice.service.external.dto.DiscountResponseDto;
import com.order.management.accountingservice.service.external.dto.ProductPriceDto;
import com.order.management.accountingservice.dto.ProductRequestDto;
import com.order.management.accountingservice.dto.ProductTotalPriceDto;
import com.order.management.accountingservice.mapper.OrderPriceMapper;
import com.order.management.accountingservice.mapper.ProductPriceMapper;
import com.order.management.accountingservice.service.AccountingService;
import com.order.management.accountingservice.service.ProductService;
import com.order.management.common.constant.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountingServiceImpl implements AccountingService {
    private final ProductService productService;
    private final DiscountService discountService;
    private final AccountingManagement accountingManagement;
    private final ProductPriceMapper productPriceMapper;
    private final OrderPriceMapper orderPriceMapper;


    @Override
    @RabbitListener(queues = "${order.accounting.price.queue}")
    public void calculateCost(OrderRecordDto orderRecordDto) {
        List<ProductPriceDto> productPriceDtos = getPrices(orderRecordDto);

        // map ProductTotalPriceDto
        List<ProductTotalPriceDto> productTotalPriceDtos = productPriceMapper.productRequestDtoToProductTotalPriceDto(orderRecordDto.getCart());

        // set product price and total price for each product
        productTotalPriceDtos.stream().parallel()
                .forEach(totalPriceDto -> setProductPriceAndTotal(totalPriceDto, productPriceDtos));

        // map OrderPrice
        OrderPrice orderPrice = orderPriceMapper.orderRecordDtoToOrderPriceDto(orderRecordDto);
        orderPrice.setCart(productTotalPriceDtos);

        // get discount and set amount discount
        DiscountResponseDto discount = discountService.getDiscountDetail(orderRecordDto.getDiscountCode());
        orderPrice.setDiscount(discount.getDiscountAmount());

        // calculate order total prices
        orderPrice.setTotalRawPrice(orderPrice.getCart().stream().mapToDouble(ProductTotalPriceDto::getTotalPrice).sum());
        orderPrice.setTotalPrice(orderPrice.getTotalRawPrice() - orderPrice.getDiscount());

        // set status as priced
        orderPrice.setStatus(OrderStatus.PRICED);

        log.info("Order Prices are calculated, [{}]", orderPrice);
        accountingManagement.sendToPayment(orderPrice);

        // todo send it to billing
    }

    private void setProductPriceAndTotal(ProductTotalPriceDto totalPriceDto, List<ProductPriceDto> productPriceDtos) {
        productPriceDtos.stream()
                .filter(priceDto -> totalPriceDto.getProductId().equals(priceDto.getProductId()))
                .findFirst()
                .ifPresent(priceDto -> {
                    totalPriceDto.setPrice(priceDto.getPrice());
                    totalPriceDto.setTotalPrice(totalPriceDto.getPrice() * totalPriceDto.getQuantity());
                });
    }

    private List<ProductPriceDto> getPrices(OrderRecordDto orderRecordDto) {
        List<Long> ids = orderRecordDto.getCart().stream()
                .map(ProductRequestDto::getProductId)
                .toList();
         return productService.getPriceList(ids);
    }
}

