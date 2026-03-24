package com.example.demo.review.service;

import com.example.demo.order.entity.Order;
import com.example.demo.order.repository.OrderRepository;
import com.example.demo.review.entity.Review;
import com.example.demo.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseReviewService implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    // 상품별 리뷰 조회
    @Override
    public List<Review> findByItemId(Integer itemId) {
        return reviewRepository.findByItemIdOrderByCreatedDesc(itemId);
    }

    // 회원별 리뷰 조회
    @Override
    public List<Review> findByLoginId(String loginId) {
        return reviewRepository.findByLoginIdOrderByCreatedDesc(loginId);
    }

    // 전체 리뷰 조회 (관리자)
    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    // 리뷰 등록
    @Override
    public Review save(Integer itemId, String loginId, String name, String content, Integer rating) {
        Review review = new Review(itemId, loginId, name, content, rating);
        return reviewRepository.save(review);
    }

    // 리뷰 수정
    @Override
    public void update(Integer id, String content, Integer rating) {
        Review review = reviewRepository.findById(id).orElseThrow();
        review.update(content, rating);
        reviewRepository.save(review);
    }

    // 리뷰 삭제
    @Override
    public void delete(Integer id) {
        reviewRepository.deleteById(id);
    }

    // 구매 여부 확인 (배송완료 상태인 주문에 해당 상품이 있는지)
    @Override
    public boolean hasPurchased(String loginId, Integer itemId) {
        List<Order> orders = orderRepository.findByLoginIdOrderByCreatedDesc(loginId);
        return orders.stream()
                .filter(o -> "배송완료".equals(o.getStatus()))
                .flatMap(o -> o.getOrderItems().stream())
                .anyMatch(i -> i.getItemId().equals(itemId));
    }

    // 이미 리뷰 작성 여부 확인
    @Override
    public boolean hasReviewed(String loginId, Integer itemId) {
        return reviewRepository.existsByItemIdAndLoginId(itemId, loginId);
    }
}