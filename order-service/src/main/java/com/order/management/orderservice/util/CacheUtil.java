package com.order.management.orderservice.util;

import java.util.UUID;

public final class CacheUtil {
    public final static String VALIDATION_CACHE_ORDER_KEY = "VALIDATION_RESULT_ORDER_ID_%s";

    public static String getCacheKey(String key, UUID orderId) {
        return String.format(key, orderId);
    }
}
