package com.example.demo.wishlist.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "wishlists", uniqueConstraints = @UniqueConstraint(columnNames = {"loginId", "itemId"}))
public class Wishlist {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private Integer itemId;

    @Column(nullable = false)
    private LocalDateTime created;

    public Wishlist() {}

    public Wishlist(String loginId, Integer itemId) {
        this.loginId = loginId;
        this.itemId = itemId;
        this.created = LocalDateTime.now();
    }
}
