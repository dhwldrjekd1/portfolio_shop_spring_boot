package com.example.demo.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String payment;

    private String cardNumber;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private String status; // 주문접수, 배송중, 배송완료, 취소

    @Column(nullable = false)
    private LocalDateTime created;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order() {}

    public Order(String loginId, String name, String address, String payment, String cardNumber, Integer amount) {
        this.loginId = loginId;
        this.name = name;
        this.address = address;
        this.payment = payment;
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.status = "주문접수";
        this.created = LocalDateTime.now();
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}