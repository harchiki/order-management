package com.order.management.orderservice.model;

import com.order.management.common.constant.OrderStatus;
import com.order.management.common.constant.PaymentType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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

    // Added fetch EAGER on cart for simplicity due to lazy loading issue in generated code by mapstruct
    // todo reconsider the solution in future
    @OneToMany(mappedBy="order", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private Set<ProductRequest> cart;

    @Column
    private String discountCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentType paymentType;

   //  todo will be removed from here after moving them billing service
    /*@Column
    private double totalPrice;

    @Column
    private double appliedDiscount;

    @Column
    private double paid;*/

    public Order() {
        this.id = UUID.randomUUID();
    }
}
