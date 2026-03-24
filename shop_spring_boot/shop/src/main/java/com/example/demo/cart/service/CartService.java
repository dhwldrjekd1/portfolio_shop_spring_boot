package com.example.demo.cart.service;

import com.example.demo.cart.entity.Cart;
import java.util.List;

public interface CartService {
    List<Cart> findAll(String loginId);
    Cart findByLoginIdAndItemId(String loginId, Integer itemId);
    void save(String loginId, Integer itemId, Integer quantity, String color, String size);
    void updateQuantity(Integer id, Integer quantity);
    void delete(Integer id);
    void deleteAll(String loginId);
}