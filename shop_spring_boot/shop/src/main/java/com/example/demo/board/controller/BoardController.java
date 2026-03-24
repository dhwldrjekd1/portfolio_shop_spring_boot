package com.example.demo.board.controller;

import com.example.demo.board.entity.Board;
import com.example.demo.board.entity.Comment;
import com.example.demo.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Board> list = boardService.findAll();
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, String> body) {
        try {
            Board board = boardService.save(
                    body.get("title"),
                    body.get("content"),
                    body.get("loginId"),
                    body.get("name")
            );
            return ResponseEntity.ok(Map.of("success", true, "id", board.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            boardService.update(id, body.get("title"), body.get("content"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            boardService.delete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/{boardId}/comments")
    public ResponseEntity<?> findComments(@PathVariable Integer boardId) {
        List<Comment> list = boardService.findComments(boardId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{boardId}/comments")
    public ResponseEntity<?> saveComment(@PathVariable Integer boardId, @RequestBody Map<String, String> body) {
        try {
            Comment comment = boardService.saveComment(
                    boardId,
                    body.get("content"),
                    body.get("loginId"),
                    body.get("name")
            );
            return ResponseEntity.ok(Map.of("success", true, "id", comment.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer id) {
        try {
            boardService.deleteComment(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}