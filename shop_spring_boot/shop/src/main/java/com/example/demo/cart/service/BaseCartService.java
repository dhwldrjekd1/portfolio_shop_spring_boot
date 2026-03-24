package com.example.demo.cart.service;

import com.example.demo.cart.entity.Cart;
import com.example.demo.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseCartService implements CartService {

    private final CartRepository cartRepository;

    @Override
    public List<Cart> findAll(String loginId) {
        return cartRepository.findByLoginIdOrderByCreatedDesc(loginId);
    }

    @Override
    public Cart findByLoginIdAndItemId(String loginId, Integer itemId) {
        return cartRepository.findByLoginIdAndItemId(loginId, itemId).orElse(null);
    }

    @Override
    public void save(String loginId, Integer itemId, Integer quantity, String color, String size) {
        Cart cart = new Cart(loginId, itemId, quantity, color, size);
        cartRepository.save(cart);
    }

    @Override
    public void updateQuantity(Integer id, Integer quantity) {
        Cart cart = cartRepository.findById(id).orElseThrow();
        cart.updateQuantity(quantity);
        cartRepository.save(cart);
    }

    @Override
    public void delete(Integer id) {
        cartRepository.deleteById(id);
    }

    @Override
    public void deleteAll(String loginId) {
        cartRepository.deleteByLoginId(loginId);
    }
}