package com.example.demo.cart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private Integer itemId;

    @Column(nullable = false)
    private Integer quantity;

    private String color;

    private String size;

    @Column(nullable = false)
    private LocalDateTime created;

    public Cart() {}

    public Cart(String loginId, Integer itemId, Integer quantity, String color, String size) {
        this.loginId = loginId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.color = color;
        this.size = size;
        this.created = LocalDateTime.now();
    }

    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}