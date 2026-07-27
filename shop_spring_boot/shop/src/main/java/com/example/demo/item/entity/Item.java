package com.example.demo.item.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private String imagePath; // 상품 이미지 경로 (nullable)

    @Column(nullable = false)
    private Double price;

    private Double discountRate; // 할인율 (0~100)

    @Column(nullable = false)
    private Integer stock; // 재고 수량

    private String description; // 상품 설명

    private String category; // 카테고리 (all, sunglasses, eyeglasses)

    private String badge; // 뱃지 (NEW, BEST, SALE 등)

    @Column(columnDefinition = "TEXT")
    private String detailsJson; // 세부정보 JSON 문자열 ({"소재":"...", "렌즈":"..."})

    @Column(nullable = false)
    private LocalDateTime createdAt; // 등록일시

    // ===== 기본 생성자 (JPA 필수) =====
    public Item() {}

    // ===== 상품 등록용 생성자 =====
    public Item(String name, String imagePath, Double price, Double discountRate,
                Integer stock, String description, String category, String badge) {
        validatePrice(price);
        discountRate = validateDiscountRate(discountRate);
        this.name         = name;
        this.imagePath    = imagePath;
        this.price        = price;
        this.discountRate = discountRate;
        this.stock        = stock != null ? stock : 0;
        this.description  = description;
        this.category     = category;
        this.badge        = badge;
        this.detailsJson  = null;
        this.createdAt    = LocalDateTime.now();
    }

    // ===== 상품 기본 정보 수정 =====
    public void update(String name, String imagePath, Double price, Double discountRate,
                       Integer stock, String description, String category, String badge) {
        validatePrice(price);
        discountRate = validateDiscountRate(discountRate);
        this.name         = name;
        if (imagePath != null && !imagePath.isEmpty()) this.imagePath = imagePath;
        this.price        = price;
        this.discountRate = discountRate;
        this.stock        = stock != null ? stock : 0;
        this.description  = description;
        this.category     = category;
        this.badge        = badge;
    }

    // 가격/할인율이 잘못 저장되면 결제 금액 계산이 통째로 깨지므로, 저장 직전에 항상 검증
    private static void validatePrice(Double price) {
        if (price == null || price < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
    }

    private static Double validateDiscountRate(Double discountRate) {
        if (discountRate == null) return 0.0;
        if (discountRate < 0 || discountRate > 100) {
            throw new IllegalArgumentException("할인율은 0~100 사이여야 합니다.");
        }
        return discountRate;
    }

    // ===== 세부정보 수정 (JSON 문자열로 저장) =====
    public void updateDetails(String detailsJson) {
        this.detailsJson = detailsJson;
    }

    // ===== 재고 수정 =====
    public void updateStock(Integer stock) {
        this.stock = stock;
    }

    // ===== 할인율 수정 =====
    public void updateDiscountRate(Double discountRate) {
        this.discountRate = validateDiscountRate(discountRate);
    }
}