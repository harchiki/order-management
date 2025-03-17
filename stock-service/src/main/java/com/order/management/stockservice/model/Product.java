package com.order.management.stockservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Product {
    @Id
    private Long productId;

    @Column
    private String name;

    @Column
    private long quantity;

    @Column
    private double price;
}
