package com.example.demo.review.service;

import com.example.demo.review.entity.Review;
import com.example.demo.review.repository.ReviewRepository;
import java.util.List;

public interface ReviewService {
    // 상품별 리뷰 조회
    List<Review> findByItemId(Integer itemId);
    // 회원별 리뷰 조회
    List<Review> findByLoginId(String loginId);
    // 전체 리뷰 조회 (관리자)
    List<Review> findAll();
    // 상품 목록 평균 별점 계산용 - itemId/rating만 (공개)
    List<ReviewRepository.RatingOnly> findAllRatings();
    // 단일 리뷰 조회
    Review findById(Integer id);
    // 리뷰 등록
    Review save(Integer itemId, String loginId, String name, String content, Integer rating);
    // 리뷰 수정
    void update(Integer id, String content, Integer rating);
    // 리뷰 삭제
    void delete(Integer id);
    // 구매 여부 확인 (주문완료 상태인 주문에 해당 상품이 있는지)
    boolean hasPurchased(String loginId, Integer itemId);
    // 이미 리뷰 작성 여부 확인
    boolean hasReviewed(String loginId, Integer itemId);
}