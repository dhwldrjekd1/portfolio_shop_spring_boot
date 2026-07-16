package com.example.demo.wishlist.repository;

import com.example.demo.wishlist.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    List<Wishlist> findByLoginIdOrderByCreatedDesc(String loginId);
    Optional<Wishlist> findByLoginIdAndItemId(String loginId, Integer itemId);

    @Transactional
    void deleteByLoginIdAndItemId(String loginId, Integer itemId);
}
