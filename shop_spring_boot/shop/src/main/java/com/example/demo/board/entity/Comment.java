package com.example.demo.board.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime created;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public Comment() {}

    public Comment(String content, String loginId, String name, Board board) {
        this.content = content;
        this.loginId = loginId;
        this.name = name;
        this.board = board;
        this.created = LocalDateTime.now();
    }

    public void update(String content) {
        this.content = content;
    }

}