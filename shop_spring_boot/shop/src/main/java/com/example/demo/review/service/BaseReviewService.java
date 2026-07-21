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

    // 상품 목록 평균 별점 계산용 - itemId/rating만 (공개)
    @Override
    public List<ReviewRepository.RatingOnly> findAllRatings() {
        return reviewRepository.findAllProjectedBy();
    }

    // 단일 리뷰 조회
    @Override
    public Review findById(Integer id) {
        return reviewRepository.findById(id).orElseThrow();
    }

    // 리뷰 등록 (배송완료 구매 확인 + 중복 작성 방지를 서버에서도 재검증 - 프론트의 사전 체크는
    // 우회 가능하므로, 실제 저장 시점에 동일한 조건을 다시 확인해야 신뢰할 수 있다)
    @Override
    public Review save(Integer itemId, String loginId, String name, String content, Integer rating) {
        if (!hasPurchased(loginId, itemId)) {
            throw new IllegalStateException("배송완료된 주문에서만 리뷰를 작성할 수 있습니다.");
        }
        if (hasReviewed(loginId, itemId)) {
            throw new IllegalStateException("이미 작성한 리뷰가 있습니다.");
        }
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