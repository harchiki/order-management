package com.order.management.orderservice.model;

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
public class ProductRequest {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;

    @Column
    private long productId;

    @Column
    private long quantity;
}
