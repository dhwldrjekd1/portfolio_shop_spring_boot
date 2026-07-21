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
        this.quantity = validQuantity(quantity);
        this.color = color;
        this.size = size;
        this.created = LocalDateTime.now();
    }

    public void updateQuantity(Integer quantity) {
        this.quantity = validQuantity(quantity);
    }

    // 수량은 항상 1개 이상이어야 한다. 신규 등록(생성자)과 수량 변경(updateQuantity) 양쪽 모두
    // 여기를 거치므로, 장바구니 추가 시 기존 수량과 합산되는 경로까지 함께 보호된다.
    private static Integer validQuantity(Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        return quantity;
    }
}