package com.example.demo.inquiry.controller;

import com.example.demo.inquiry.entity.Inquiry;
import com.example.demo.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inquiry")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    // 문의 등록
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, String> body) {
        try {
            Inquiry inquiry = inquiryService.save(
                    body.get("type"),
                    body.get("title"),
                    body.get("content"),
                    body.get("loginId"),
                    body.get("name")
            );
            return ResponseEntity.ok(Map.of("success", true, "id", inquiry.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 내 문의 목록
    @GetMapping("/my/{loginId}")
    public ResponseEntity<?> findByLoginId(@PathVariable String loginId) {
        List<Inquiry> list = inquiryService.findByLoginId(loginId);
        return ResponseEntity.ok(list);
    }

    // 전체 문의 목록 (관리자)
    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        List<Inquiry> list = inquiryService.findAll();
        return ResponseEntity.ok(list);
    }

    // 답글 등록 (관리자)
    @PostMapping("/{id}/reply")
    public ResponseEntity<?> reply(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            inquiryService.reply(id, body.get("reply"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    // 문의 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            inquiryService.update(id, body.get("content"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 문의 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            inquiryService.delete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}