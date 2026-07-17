package com.example.demo.cart.controller;

import com.example.demo.cart.entity.Cart;
import com.example.demo.cart.service.CartService;
import com.example.demo.common.auth.SessionAuth;
import jakarta.servlet.http.HttpServletRequest;
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

    // 장바구니 조회 (본인 또는 관리자)
    @GetMapping("/{loginId}")
    public ResponseEntity<?> findAll(@PathVariable String loginId, HttpServletRequest request) {
        if (!SessionAuth.isSelfOrAdmin(request, loginId)) return SessionAuth.forbidden();
        List<Cart> list = cartService.findAll(loginId);
        return ResponseEntity.ok(list);
    }

    // 장바구니 추가 (로그인 필요, 본인 명의로만 추가)
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        if (!SessionAuth.isLoggedIn(request)) return SessionAuth.unauthorized();
        try {
            String loginId = SessionAuth.currentLoginId(request);
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

    // 수량 변경 (본인 또는 관리자)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuantity(@PathVariable Integer id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        try {
            Cart cart = cartService.findById(id);
            if (!SessionAuth.isSelfOrAdmin(request, cart.getLoginId())) return SessionAuth.forbidden();
            Integer quantity = (Integer) body.get("quantity");
            cartService.updateQuantity(id, quantity);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 장바구니 삭제 (본인 또는 관리자)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, HttpServletRequest request) {
        try {
            Cart cart = cartService.findById(id);
            if (!SessionAuth.isSelfOrAdmin(request, cart.getLoginId())) return SessionAuth.forbidden();
            cartService.delete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 장바구니 전체 삭제 (본인 또는 관리자)
    @DeleteMapping("/clear/{loginId}")
    public ResponseEntity<?> deleteAll(@PathVariable String loginId, HttpServletRequest request) {
        if (!SessionAuth.isSelfOrAdmin(request, loginId)) return SessionAuth.forbidden();
        try {
            cartService.deleteAll(loginId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
