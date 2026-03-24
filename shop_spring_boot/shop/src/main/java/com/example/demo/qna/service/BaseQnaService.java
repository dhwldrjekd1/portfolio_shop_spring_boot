package com.example.demo.qna.service;

import com.example.demo.qna.entity.Qna;
import com.example.demo.qna.repository.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseQnaService implements QnaService {

    private final QnaRepository qnaRepository;

    @Override
    public Qna save(String category, String title, String content) {
        Qna qna = new Qna(category, title, content);
        return qnaRepository.save(qna);
    }

    @Override
    public List<Qna> findAll() {
        return qnaRepository.findAllByOrderByCreatedDesc();
    }

    @Override
    public List<Qna> findByCategory(String category) {
        return qnaRepository.findByCategoryOrderByCreatedDesc(category);
    }

    @Override
    public void update(Integer id, String category, String title, String content) {
        Qna qna = qnaRepository.findById(id).orElseThrow();
        qna.update(category, title, content);
        qnaRepository.save(qna);
    }

    @Override
    public void delete(Integer id) {
        qnaRepository.deleteById(id);
    }
}