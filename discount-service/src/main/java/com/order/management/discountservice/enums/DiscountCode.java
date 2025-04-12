package com.order.management.discountservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum DiscountCode {
    NO_DISCOUNT("NO_DISCOUNT", 0),
    DISCOUNT_100("DISCOUNT100", 100),
    DISCOUNT_250("DISCOUNT250", 250),
    DISCOUNT_500("DISCOUNT500", 500),
    DISCOUNT_1000("DISCOUNT1000", 1000);

    private final String discountCode;
    private final double discount;

    public static Optional<DiscountCode> findByDiscountCode(String discountCode) {
        return Arrays.stream(values())
                .filter(discount -> Objects.nonNull(discountCode) && discountCode.equals(discount.getDiscountCode()))
                .findFirst();
    }
}
