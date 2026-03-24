package com.example.demo.cart.repository;

import com.example.demo.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByLoginIdOrderByCreatedDesc(String loginId);
    Optional<Cart> findByLoginIdAndItemId(String loginId, Integer itemId);

    @Transactional
    void deleteByLoginId(String loginId);
}