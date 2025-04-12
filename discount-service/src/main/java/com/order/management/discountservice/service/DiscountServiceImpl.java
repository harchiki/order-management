package com.order.management.discountservice.service;

import com.order.management.common.constant.ValidationStatus;
import com.order.management.discountservice.dto.DiscountResponseDto;
import com.order.management.discountservice.dto.DiscountValidationResult;
import com.order.management.discountservice.dto.OrderMessage;

import com.order.management.discountservice.enums.DiscountCode;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class DiscountServiceImpl implements DiscountService {
    private final RabbitTemplate rabbitTemplate;

    @Value("${order.validation.response.exchange}")
    private String validationResponseExchange;

    @Override
    @RabbitListener(queues = "${order.validation.discount.queue}")
    public void consumeOrder(OrderMessage order) {
        log.info("Consuming the order message : [{}]", order);

        DiscountValidationResult result = new DiscountValidationResult();
        result.setOrderId(order.getOrderId());

        ValidationStatus status = verifyDiscountIfExist(order.getDiscountCode());
        result.setDiscountStatus(status);

        rabbitTemplate.convertAndSend(validationResponseExchange, "", result);
    }

    @Override
    public DiscountResponseDto getDiscount(String discountCode) {
        DiscountCode discount = DiscountCode.findByDiscountCode(discountCode)
                .orElse(DiscountCode.NO_DISCOUNT);

        DiscountResponseDto responseDto = new DiscountResponseDto();
        responseDto.setDiscountCode(discount.getDiscountCode());
        responseDto.setDiscountAmount(discount.getDiscount());
        return responseDto;
    }

    private ValidationStatus verifyDiscountIfExist(String discountCode) {
        if (StringUtils.isBlank(discountCode)) {
            log.info("No discountCode");
            return ValidationStatus.OKAY;
        }
        Optional<DiscountCode> optDiscount = DiscountCode.findByDiscountCode(discountCode);
        if (optDiscount.isEmpty()) {
            log.info("Invalid discountCode : discountCode : {}", discountCode);
            return ValidationStatus.REJECTED;
        }
        log.info("Validated discountCode : discountCode : {}", discountCode);
        return ValidationStatus.OKAY;
    }
}
