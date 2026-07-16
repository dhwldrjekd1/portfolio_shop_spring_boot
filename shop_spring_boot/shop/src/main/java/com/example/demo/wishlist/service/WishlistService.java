package com.example.demo.wishlist.service;

import com.example.demo.wishlist.entity.Wishlist;
import java.util.List;

public interface WishlistService {
    List<Wishlist> findAll(String loginId);
    void add(String loginId, Integer itemId);
    void remove(String loginId, Integer itemId);
}
