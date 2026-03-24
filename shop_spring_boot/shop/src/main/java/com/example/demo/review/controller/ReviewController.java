package com.example.demo.review.controller;

import com.example.demo.review.entity.Review;
import com.example.demo.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 상품별 리뷰 조회
    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> findByItemId(@PathVariable Integer itemId) {
        List<Review> list = reviewService.findByItemId(itemId);
        return ResponseEntity.ok(list);
    }

    // 회원별 리뷰 조회 (마이페이지)
    @GetMapping("/member/{loginId}")
    public ResponseEntity<?> findByLoginId(@PathVariable String loginId) {
        List<Review> list = reviewService.findByLoginId(loginId);
        return ResponseEntity.ok(list);
    }

    // 전체 리뷰 조회 (관리자)
    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        List<Review> list = reviewService.findAll();
        return ResponseEntity.ok(list);
    }

    // 리뷰 등록
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, Object> body) {
        try {
            Integer itemId  = (Integer) body.get("itemId");
            String loginId  = (String) body.get("loginId");
            String name     = (String) body.get("name");
            String content  = (String) body.get("content");
            Integer rating  = (Integer) body.get("rating");
            Review review = reviewService.save(itemId, loginId, name, content, rating);
            return ResponseEntity.ok(Map.of("success", true, "id", review.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 리뷰 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            String content  = (String) body.get("content");
            Integer rating  = (Integer) body.get("rating");
            reviewService.update(id, content, rating);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            reviewService.delete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 구매 여부 확인
    @GetMapping("/check/purchased/{loginId}/{itemId}")
    public ResponseEntity<?> hasPurchased(@PathVariable String loginId, @PathVariable Integer itemId) {
        boolean result = reviewService.hasPurchased(loginId, itemId);
        return ResponseEntity.ok(Map.of("hasPurchased", result));
    }

    // 리뷰 작성 여부 확인
    @GetMapping("/check/reviewed/{loginId}/{itemId}")
    public ResponseEntity<?> hasReviewed(@PathVariable String loginId, @PathVariable Integer itemId) {
        boolean result = reviewService.hasReviewed(loginId, itemId);
        return ResponseEntity.ok(Map.of("hasReviewed", result));
    }
}