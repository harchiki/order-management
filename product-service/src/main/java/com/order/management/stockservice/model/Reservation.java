package com.order.management.stockservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private UUID orderId;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    @Column
    private long quantity;
}
