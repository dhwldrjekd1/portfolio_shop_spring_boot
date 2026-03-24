package com.example.demo.cart.controller;

import com.example.demo.cart.entity.Cart;
import com.example.demo.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 장바구니 조회
    @GetMapping("/{loginId}")
    public ResponseEntity<?> findAll(@PathVariable String loginId) {
        List<Cart> list = cartService.findAll(loginId);
        return ResponseEntity.ok(list);
    }

    // 장바구니 추가
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, Object> body) {
        try {
            String loginId = (String) body.get("loginId");
            Integer itemId = (Integer) body.get("itemId");
            Integer quantity = (Integer) body.get("quantity");
            String color = (String) body.get("color");
            String size = (String) body.get("size");

            // 이미 있으면 수량 업데이트
            Cart existing = cartService.findByLoginIdAndItemId(loginId, itemId);
            if (existing != null) {
                cartService.updateQuantity(existing.getId(), existing.getQuantity() + quantity);
            } else {
                cartService.save(loginId, itemId, quantity, color, size);
            }
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 수량 변경
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuantity(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            Integer quantity = (Integer) body.get("quantity");
            cartService.updateQuantity(id, quantity);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 장바구니 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            cartService.delete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 장바구니 전체 삭제
    @DeleteMapping("/clear/{loginId}")
    public ResponseEntity<?> deleteAll(@PathVariable String loginId) {
        try {
            cartService.deleteAll(loginId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}