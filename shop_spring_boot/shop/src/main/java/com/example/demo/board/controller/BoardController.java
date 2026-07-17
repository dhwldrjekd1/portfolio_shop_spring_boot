package com.example.demo.board.controller;

import com.example.demo.common.error.ApiError;
import com.example.demo.board.entity.Board;
import com.example.demo.board.entity.Comment;
import com.example.demo.board.service.BoardService;
import com.example.demo.common.auth.SessionAuth;
import jakarta.servlet.http.HttpServletRequest;
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

    // 게시글 작성 (로그인 필요, 본인 명의로만 작성)
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, String> body, HttpServletRequest request) {
        if (!SessionAuth.isLoggedIn(request)) return SessionAuth.unauthorized();
        try {
            Board board = boardService.save(
                    body.get("title"),
                    body.get("content"),
                    SessionAuth.currentLoginId(request),
                    body.get("name")
            );
            return ResponseEntity.ok(Map.of("success", true, "id", board.getId()));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }

    // 게시글 수정 (작성자 본인 또는 관리자)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        try {
            Board board = boardService.findById(id);
            if (!SessionAuth.isSelfOrAdmin(request, board.getLoginId())) return SessionAuth.forbidden();
            boardService.update(id, body.get("title"), body.get("content"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }

    // 게시글 삭제 (작성자 본인 또는 관리자)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, HttpServletRequest request) {
        try {
            Board board = boardService.findById(id);
            if (!SessionAuth.isSelfOrAdmin(request, board.getLoginId())) return SessionAuth.forbidden();
            boardService.delete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }

    @GetMapping("/{boardId}/comments")
    public ResponseEntity<?> findComments(@PathVariable Integer boardId) {
        List<Comment> list = boardService.findComments(boardId);
        return ResponseEntity.ok(list);
    }

    // 댓글 작성 (로그인 필요, 본인 명의로만 작성)
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<?> saveComment(@PathVariable Integer boardId, @RequestBody Map<String, String> body, HttpServletRequest request) {
        if (!SessionAuth.isLoggedIn(request)) return SessionAuth.unauthorized();
        try {
            Comment comment = boardService.saveComment(
                    boardId,
                    body.get("content"),
                    SessionAuth.currentLoginId(request),
                    body.get("name")
            );
            return ResponseEntity.ok(Map.of("success", true, "id", comment.getId()));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }

    // 댓글 삭제 (작성자 본인 또는 관리자)
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer id, HttpServletRequest request) {
        try {
            Comment comment = boardService.findCommentById(id);
            if (!SessionAuth.isSelfOrAdmin(request, comment.getLoginId())) return SessionAuth.forbidden();
            boardService.deleteComment(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }
}
