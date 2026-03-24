package com.example.demo.item.controller;

import com.example.demo.item.entity.Item;
import com.example.demo.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // 전체 상품 조회
    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Item> list = itemService.findAll();
        return ResponseEntity.ok(list);
    }

    // 단일 상품 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        try {
            Item item = itemService.findById(id);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 상품 등록 (관리자)
    @PostMapping
    public ResponseEntity<?> save(
            @RequestParam String name,
            @RequestParam Double price,
            @RequestParam(defaultValue = "0") Double discountRate,
            @RequestParam(defaultValue = "0") Integer stock,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(defaultValue = "NEW") String badge
    ) {
        try {
            Item item = itemService.save(name, price, discountRate, stock, description, image, imageUrl, category, badge);
            return ResponseEntity.ok(Map.of("success", true, "id", item.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 상품 수정 (관리자)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestParam Double price,
            @RequestParam(defaultValue = "0") Double discountRate,
            @RequestParam(defaultValue = "0") Integer stock,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(defaultValue = "NEW") String badge
    ) {
        try {
            itemService.update(id, name, price, discountRate, stock, description, image, imageUrl, category, badge);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 상품 삭제 (관리자)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            itemService.delete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 재고 수정 (관리자)
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            Integer stock = (Integer) body.get("stock");
            itemService.updateStock(id, stock);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 할인율 수정 (관리자)
    @PutMapping("/{id}/discount")
    public ResponseEntity<?> updateDiscountRate(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            Double discountRate = ((Number) body.get("discountRate")).doubleValue();
            itemService.updateDiscountRate(id, discountRate);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    // 세부정보 수정 (관리자)
    @PutMapping("/{id}/details")
    public ResponseEntity<?> updateDetails(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body)
    {
        try {
            String detailsJson = (String) body.get("detailsJson");
            itemService.updateDetails(id, detailsJson);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}