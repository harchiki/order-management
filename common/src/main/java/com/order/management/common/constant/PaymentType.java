package com.order.management.common.constant;

public enum PaymentType {
    CREDIT_CARD("credit-card", "payment.card"),

    BANK_TRANSFER("bank-transfer", "payment.bank-transfer"),

    // digital wallets
    PAYPAL("paypal", "payment.wallet.paypal"),
    APPLE_PAY("apple-pay", "payment.wallet.apple-pay"),
    GOOGLE_PAY("google-pay", "payment.wallet.google-pay");

    private final String name;
    private final String key;

    PaymentType(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }
}
