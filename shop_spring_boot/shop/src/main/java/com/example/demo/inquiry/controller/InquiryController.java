package com.example.demo.inquiry.controller;

import com.example.demo.common.error.ApiError;
import com.example.demo.common.auth.SessionAuth;
import com.example.demo.inquiry.entity.Inquiry;
import com.example.demo.inquiry.service.InquiryService;
import jakarta.servlet.http.HttpServletRequest;
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

    // 문의 등록 (로그인 필요, 본인 명의로만 등록)
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, String> body, HttpServletRequest request) {
        if (!SessionAuth.isLoggedIn(request)) return SessionAuth.unauthorized();
        try {
            String loginId = SessionAuth.currentLoginId(request);
            Inquiry inquiry = inquiryService.save(
                    body.get("type"),
                    body.get("title"),
                    body.get("content"),
                    loginId,
                    body.get("name")
            );
            return ResponseEntity.ok(Map.of("success", true, "id", inquiry.getId()));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }

    // 내 문의 목록 (본인 또는 관리자)
    @GetMapping("/my/{loginId}")
    public ResponseEntity<?> findByLoginId(@PathVariable String loginId, HttpServletRequest request) {
        if (!SessionAuth.isSelfOrAdmin(request, loginId)) return SessionAuth.forbidden();
        List<Inquiry> list = inquiryService.findByLoginId(loginId);
        return ResponseEntity.ok(list);
    }

    // 전체 문의 목록 (관리자)
    @GetMapping("/all")
    public ResponseEntity<?> findAll(HttpServletRequest request) {
        if (!SessionAuth.isAdmin(request)) return SessionAuth.forbidden();
        List<Inquiry> list = inquiryService.findAll();
        return ResponseEntity.ok(list);
    }

    // 답글 등록 (관리자)
    @PostMapping("/{id}/reply")
    public ResponseEntity<?> reply(@PathVariable Integer id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        if (!SessionAuth.isAdmin(request)) return SessionAuth.forbidden();
        try {
            inquiryService.reply(id, body.get("reply"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }
    // 문의 수정 (작성자 본인 또는 관리자)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        try {
            Inquiry inquiry = inquiryService.findById(id);
            if (!SessionAuth.isSelfOrAdmin(request, inquiry.getLoginId())) return SessionAuth.forbidden();
            inquiryService.update(id, body.get("content"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }

    // 문의 삭제 (작성자 본인 또는 관리자)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, HttpServletRequest request) {
        try {
            Inquiry inquiry = inquiryService.findById(id);
            if (!SessionAuth.isSelfOrAdmin(request, inquiry.getLoginId())) return SessionAuth.forbidden();
            inquiryService.delete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }
}
