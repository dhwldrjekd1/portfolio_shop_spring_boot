package com.example.demo.wishlist.controller;

import com.example.demo.common.error.ApiError;
import com.example.demo.common.auth.SessionAuth;
import com.example.demo.wishlist.entity.Wishlist;
import com.example.demo.wishlist.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    // 위시리스트 조회 (본인 또는 관리자)
    @GetMapping("/{loginId}")
    public ResponseEntity<?> findAll(@PathVariable String loginId, HttpServletRequest request) {
        if (!SessionAuth.isSelfOrAdmin(request, loginId)) return SessionAuth.forbidden();
        List<Wishlist> list = wishlistService.findAll(loginId);
        return ResponseEntity.ok(list);
    }

    // 위시리스트 추가 (로그인 필요, 본인 명의로만 추가)
    @PostMapping
    public ResponseEntity<?> add(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        if (!SessionAuth.isLoggedIn(request)) return SessionAuth.unauthorized();
        try {
            String loginId = SessionAuth.currentLoginId(request);
            Integer itemId = (Integer) body.get("itemId");
            wishlistService.add(loginId, itemId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }

    // 위시리스트 삭제 (본인 또는 관리자)
    @DeleteMapping("/{loginId}/{itemId}")
    public ResponseEntity<?> remove(@PathVariable String loginId, @PathVariable Integer itemId, HttpServletRequest request) {
        if (!SessionAuth.isSelfOrAdmin(request, loginId)) return SessionAuth.forbidden();
        try {
            wishlistService.remove(loginId, itemId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }
}
