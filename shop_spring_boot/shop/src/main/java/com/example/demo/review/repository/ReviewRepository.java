package com.example.demo.review.repository;

import com.example.demo.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    // 상품별 리뷰 조회 (최신순)
    List<Review> findByItemIdOrderByCreatedDesc(Integer itemId);
    // 회원별 리뷰 조회 (최신순)
    List<Review> findByLoginIdOrderByCreatedDesc(String loginId);
    // 특정 회원의 특정 상품 리뷰 존재 여부
    boolean existsByItemIdAndLoginId(Integer itemId, String loginId);
}