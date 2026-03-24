package com.example.demo.board.service;

import com.example.demo.board.entity.Board;
import com.example.demo.board.entity.Comment;
import java.util.List;

public interface BoardService {
    Board save(String title, String content, String loginId, String name);
    List<Board> findAll();
    void update(Integer id, String title, String content);
    void delete(Integer id);
    Comment saveComment(Integer boardId, String content, String loginId, String name);
    List<Comment> findComments(Integer boardId);
    void deleteComment(Integer id);
}