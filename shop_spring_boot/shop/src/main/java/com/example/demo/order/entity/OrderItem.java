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

    // 주문 당시 단가(할인 반영, 100원 단위 반올림). 이후 상품 가격/할인율이 바뀌어도 과거 매출 통계가
    // 흔들리지 않도록 주문 시점 값을 고정 저장한다. 컬럼 추가 이전에 생성된 주문은 null.
    private Integer price;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderItem() {}

    public OrderItem(Integer itemId, Integer quantity, String itemName, String color, String size, Integer price, Order order) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.itemName = itemName;
        this.color = color;
        this.size = size;
        this.price = price;
        this.order = order;
    }
}