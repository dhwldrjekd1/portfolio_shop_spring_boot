package com.example.demo.qna.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "qnas")
public class Qna {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String category;

    @Column(name = "question", nullable = false)
    private String title;

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime created;

    public Qna() {}

    public Qna(String category, String title, String content) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.created = LocalDateTime.now();
    }

    public void update(String category, String title, String content) {
        this.category = category;
        this.title = title;
        this.content = content;
    }
}