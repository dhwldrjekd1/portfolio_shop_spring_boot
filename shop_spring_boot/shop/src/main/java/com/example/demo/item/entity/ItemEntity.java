package com.example.demo.item.entity;

import com.example.demo.item.item.dto.ItemRead;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "items")
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 500, nullable = false)
    private String imagePath;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer discountRate;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    //상품 조회 Entity => DTO로 변환
    public ItemRead toRead() {
        return ItemRead.builder()
                .id(id)
                .name(name)
                .imgPath(imagePath)
                .price(price)
                .discountPer(discountRate)
                .build();
    }
}
