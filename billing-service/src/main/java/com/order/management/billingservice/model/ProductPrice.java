package com.order.management.billingservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ProductPrice {
    @Id
    @GeneratedValue
    private long id;

    @Column
    private Long productId;

    @Column
    private double price;

    @Column
    private long quantity;

    @Column
    private double totalPrice;

    @ManyToOne
    @JoinColumn(name="order_price_id")
    private OrderPrice orderPrice;
}
