package com.example.demo.qna.repository;

import com.example.demo.qna.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Integer> {
    List<Qna> findAllByOrderByCreatedDesc();
    List<Qna> findByCategoryOrderByCreatedDesc(String category);
}