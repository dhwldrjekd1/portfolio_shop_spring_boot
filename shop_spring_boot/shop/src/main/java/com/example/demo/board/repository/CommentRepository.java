package com.example.demo.board.repository;

import com.example.demo.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByBoardIdOrderByCreatedAsc(Integer boardId);
}