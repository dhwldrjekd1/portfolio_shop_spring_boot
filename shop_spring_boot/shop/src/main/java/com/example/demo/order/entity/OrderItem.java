package com.example.demo.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private Integer itemId; // 상품 id

    @Column(nullable = false)
    private Integer quantity; // 주문 수량

    private String itemName; // 상품명

    private String color; // 색상

    private String size; // 사이즈

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderItem() {}

    public OrderItem(Integer itemId, Integer quantity, String itemName, String color, String size, Order order) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.itemName = itemName;
        this.color = color;
        this.size = size;
        this.order = order;
    }
}