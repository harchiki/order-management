package com.order.management.stockservice.constant;

public final class RabbitMQConstant {
    public static final String STOCK_VALIDATION_QUEUE = "q.stock.validation";

    public static final String VALID_ORDER_EXCHANGE = "x.valid.order";
    public static final String REJECTED_ORDER_DLX = "dlx.order";
}
