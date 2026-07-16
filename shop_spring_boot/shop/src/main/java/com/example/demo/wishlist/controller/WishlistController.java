package com.example.demo.wishlist.controller;

import com.example.demo.wishlist.entity.Wishlist;
import com.example.demo.wishlist.service.WishlistService;
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

    // 위시리스트 조회
    @GetMapping("/{loginId}")
    public ResponseEntity<?> findAll(@PathVariable String loginId) {
        List<Wishlist> list = wishlistService.findAll(loginId);
        return ResponseEntity.ok(list);
    }

    // 위시리스트 추가
    @PostMapping
    public ResponseEntity<?> add(@RequestBody Map<String, Object> body) {
        try {
            String loginId = (String) body.get("loginId");
            Integer itemId = (Integer) body.get("itemId");
            wishlistService.add(loginId, itemId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 위시리스트 삭제
    @DeleteMapping("/{loginId}/{itemId}")
    public ResponseEntity<?> remove(@PathVariable String loginId, @PathVariable Integer itemId) {
        try {
            wishlistService.remove(loginId, itemId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
