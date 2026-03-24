package com.example.demo.qna.service;

import com.example.demo.qna.entity.Qna;
import java.util.List;

public interface QnaService {
    Qna save(String category, String title, String content);
    List<Qna> findAll();
    List<Qna> findByCategory(String category);
    void update(Integer id, String category, String title, String content);
    void delete(Integer id);
}