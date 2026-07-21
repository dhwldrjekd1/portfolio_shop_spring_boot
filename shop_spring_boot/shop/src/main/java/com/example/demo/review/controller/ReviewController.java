package com.example.demo.review.controller;

import com.example.demo.common.error.ApiError;
import com.example.demo.common.auth.SessionAuth;
import com.example.demo.review.entity.Review;
import com.example.demo.review.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
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

    // 회원별 리뷰 조회 (마이페이지, 본인 또는 관리자)
    @GetMapping("/member/{loginId}")
    public ResponseEntity<?> findByLoginId(@PathVariable String loginId, HttpServletRequest request) {
        if (!SessionAuth.isSelfOrAdmin(request, loginId)) return SessionAuth.forbidden();
        List<Review> list = reviewService.findByLoginId(loginId);
        return ResponseEntity.ok(list);
    }

    // 전체 리뷰 조회 (관리자)
    @GetMapping("/all")
    public ResponseEntity<?> findAll(HttpServletRequest request) {
        if (!SessionAuth.isAdmin(request)) return SessionAuth.forbidden();
        List<Review> list = reviewService.findAll();
        return ResponseEntity.ok(list);
    }

    // 상품 목록 화면의 평균 별점 계산용 (공개) - 리뷰 내용/작성자 없이 itemId/rating만 반환
    @GetMapping("/ratings")
    public ResponseEntity<?> findAllRatings() {
        return ResponseEntity.ok(reviewService.findAllRatings());
    }

    // 리뷰 등록 (로그인 필요, 본인 명의로만 등록)
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        if (!SessionAuth.isLoggedIn(request)) return SessionAuth.unauthorized();
        try {
            Integer itemId  = (Integer) body.get("itemId");
            String loginId  = SessionAuth.currentLoginId(request);
            String name     = (String) body.get("name");
            String content  = (String) body.get("content");
            Integer rating  = (Integer) body.get("rating");
            Review review = reviewService.save(itemId, loginId, name, content, rating);
            return ResponseEntity.ok(Map.of("success", true, "id", review.getId()));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }

    // 리뷰 수정 (작성자 본인 또는 관리자)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        try {
            Review review = reviewService.findById(id);
            if (!SessionAuth.isSelfOrAdmin(request, review.getLoginId())) return SessionAuth.forbidden();
            String content  = (String) body.get("content");
            Integer rating  = (Integer) body.get("rating");
            reviewService.update(id, content, rating);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }

    // 리뷰 삭제 (작성자 본인 또는 관리자)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, HttpServletRequest request) {
        try {
            Review review = reviewService.findById(id);
            if (!SessionAuth.isSelfOrAdmin(request, review.getLoginId())) return SessionAuth.forbidden();
            reviewService.delete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }

    // 구매 여부 확인 (본인 또는 관리자)
    @GetMapping("/check/purchased/{loginId}/{itemId}")
    public ResponseEntity<?> hasPurchased(@PathVariable String loginId, @PathVariable Integer itemId, HttpServletRequest request) {
        if (!SessionAuth.isSelfOrAdmin(request, loginId)) return SessionAuth.forbidden();
        boolean result = reviewService.hasPurchased(loginId, itemId);
        return ResponseEntity.ok(Map.of("hasPurchased", result));
    }

    // 리뷰 작성 여부 확인 (본인 또는 관리자)
    @GetMapping("/check/reviewed/{loginId}/{itemId}")
    public ResponseEntity<?> hasReviewed(@PathVariable String loginId, @PathVariable Integer itemId, HttpServletRequest request) {
        if (!SessionAuth.isSelfOrAdmin(request, loginId)) return SessionAuth.forbidden();
        boolean result = reviewService.hasReviewed(loginId, itemId);
        return ResponseEntity.ok(Map.of("hasReviewed", result));
    }
}
