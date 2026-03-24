package com.example.demo.notice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean important;

    @Column(nullable = false)
    private LocalDateTime created;

    public Notice() {}

    public Notice(String title, String content, boolean important) {
        this.title = title;
        this.content = content;
        this.important = important;
        this.created = LocalDateTime.now();
    }

    public void update(String title, String content, boolean important) {
        this.title = title;
        this.content = content;
        this.important = important;
    }
}