package com.example.demo.review.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer itemId;       // 상품 ID

    @Column(nullable = false)
    private String loginId;       // 작성자 아이디

    @Column(nullable = false)
    private String name;          // 작성자 이름

    @Column(nullable = false)
    private String content;       // 리뷰 내용

    @Column(nullable = false)
    private Integer rating;       // 별점 (1~5)

    @Column(nullable = false)
    private LocalDateTime created; // 작성일시

    public Review() {}

    // 리뷰 등록용 생성자
    public Review(Integer itemId, String loginId, String name, String content, Integer rating) {
        this.itemId = itemId;
        this.loginId = loginId;
        this.name = name;
        this.content = content;
        this.rating = rating;
        this.created = LocalDateTime.now();
    }

    // 리뷰 수정
    public void update(String content, Integer rating) {
        this.content = content;
        this.rating = rating;
    }
}