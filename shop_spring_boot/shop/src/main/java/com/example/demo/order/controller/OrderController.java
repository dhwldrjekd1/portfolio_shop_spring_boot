package com.example.demo.order.controller;

import com.example.demo.common.auth.SessionAuth;
import com.example.demo.order.entity.Order;
import com.example.demo.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 목록 조회 (본인 또는 관리자)
    @GetMapping("/{loginId}")
    public ResponseEntity<?> findAll(@PathVariable String loginId, HttpServletRequest request) {
        if (!SessionAuth.isSelfOrAdmin(request, loginId)) return SessionAuth.forbidden();
        List<Order> list = orderService.findAll(loginId);
        return ResponseEntity.ok(list);
    }

    // 주문 등록 (로그인 필요, 본인 명의로만 주문 생성)
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        if (!SessionAuth.isLoggedIn(request)) return SessionAuth.unauthorized();
        try {
            String loginId = SessionAuth.currentLoginId(request);
            String name = (String) body.get("name");
            String address = (String) body.get("address");
            String payment = (String) body.get("payment");
            String cardNumber = (String) body.get("cardNumber");
            Integer amount = (Integer) body.get("amount");
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");

            Order order = orderService.save(loginId, name, address, payment, cardNumber, amount, items);
            return ResponseEntity.ok(Map.of("success", true, "id", order.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    // 전체 주문 조회 (관리자)
    @GetMapping("/all")
    public ResponseEntity<?> findAll(HttpServletRequest request) {
        if (!SessionAuth.isAdmin(request)) return SessionAuth.forbidden();
        try {
            List<Order> list = orderService.findAll();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 주문 상태 변경 (관리자)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        if (!SessionAuth.isAdmin(request)) return SessionAuth.forbidden();
        try {
            orderService.updateStatus(id, body.get("status"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    // 주문 삭제 (관리자)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, HttpServletRequest request) {
        if (!SessionAuth.isAdmin(request)) return SessionAuth.forbidden();
        try {
            orderService.delete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    // 주문 취소 (본인 또는 관리자)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Integer id, HttpServletRequest request) {
        try {
            Order order = orderService.findById(id);
            if (!SessionAuth.isSelfOrAdmin(request, order.getLoginId())) return SessionAuth.forbidden();
            orderService.updateStatus(id, "취소");
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
