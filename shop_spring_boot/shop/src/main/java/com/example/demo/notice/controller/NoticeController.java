package com.example.demo.notice.controller;

import com.example.demo.notice.entity.Notice;
import com.example.demo.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 전체 조회
    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Notice> list = noticeService.findAll();
        return ResponseEntity.ok(list);
    }

    // 등록 (관리자)
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, Object> body) {
        try {
            Notice notice = noticeService.save(
                    (String) body.get("title"),
                    (String) body.get("content"),
                    (Boolean) body.getOrDefault("important", false)
            );
            return ResponseEntity.ok(Map.of("success", true, "id", notice.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 수정 (관리자)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            noticeService.update(
                    id,
                    (String) body.get("title"),
                    (String) body.get("content"),
                    (Boolean) body.getOrDefault("important", false)
            );
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 삭제 (관리자)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            noticeService.delete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}