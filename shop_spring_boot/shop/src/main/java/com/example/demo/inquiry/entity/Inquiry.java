package com.example.demo.inquiry.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "inquiries")
public class Inquiry {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column
    private String reply;

    @Column
    private LocalDateTime repliedAt;

    @Column(nullable = false)
    private String status;

    public void update(String content) {
        this.content = content;
    }

    public Inquiry() {}

    public Inquiry(String type, String title, String content, String loginId, String name) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.loginId = loginId;
        this.name = name;
        this.created = LocalDateTime.now();
        this.status = "접수중";
    }

    public void setReply(String reply) {
        this.reply = reply;
        this.repliedAt = LocalDateTime.now();
        this.status = "답변완료";
    }

    public void delete() {}
}