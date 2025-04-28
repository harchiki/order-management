package com.order.management.billingservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Entity
public class OrderPrice {
    @Id
    private UUID id;

    @Column(unique = true)
    private UUID orderId;

    @OneToMany(mappedBy="orderPrice", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private List<ProductPrice> cart;

    @Column
    private double totalRawPrice;

    @Column
    private double totalPrice;

    @Column
    private double discount;
}
