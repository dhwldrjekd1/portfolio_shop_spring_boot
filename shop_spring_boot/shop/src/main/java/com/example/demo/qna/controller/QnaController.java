package com.example.demo.qna.controller;

import com.example.demo.qna.entity.Qna;
import com.example.demo.qna.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/qna")
@RequiredArgsConstructor
public class QnaController {

    private final QnaService qnaService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Qna> list = qnaService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> findByCategory(@PathVariable String category) {
        List<Qna> list = qnaService.findByCategory(category);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, String> body) {
        try {
            Qna qna = qnaService.save(
                    body.get("category"),
                    body.get("title"),
                    body.get("content")
            );
            return ResponseEntity.ok(Map.of("success", true, "id", qna.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            qnaService.update(
                    id,
                    body.get("category"),
                    body.get("title"),
                    body.get("content")
            );
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            qnaService.delete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}