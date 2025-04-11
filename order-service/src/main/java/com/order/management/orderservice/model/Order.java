package com.order.management.orderservice.model;

import com.order.management.common.constant.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "order_")
public class Order {
    @Id
    private UUID id;

    @OneToMany(mappedBy="order", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<ProductRequest> cart;

    @Column
    private String discountCode;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column
    private double totalPrice;

    @Column
    private double appliedDiscount;

    @Column
    private double paid;

    public Order() {
        this.id = UUID.randomUUID();
    }
}
