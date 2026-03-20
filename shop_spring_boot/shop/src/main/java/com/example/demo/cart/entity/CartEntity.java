package com.example.demo.cart.entity;

import com.example.demo.cart.dto.CartRead;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "carts")
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer memberId;

    @Column(nullable = false)
    private Integer productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public CartEntity() {
    }

    public CartEntity(Integer memberId, Integer productId, Integer quantity) {
        this.memberId = memberId;
        this.productId = productId;
        this.quantity = quantity;
    }
    //장바구니 조회 DTO로 변환
    public CartRead toRead() {
        return CartRead.builder()
                .id(id)
                .productId(productId)
                .quantity(quantity)
                .build();


    }
}
