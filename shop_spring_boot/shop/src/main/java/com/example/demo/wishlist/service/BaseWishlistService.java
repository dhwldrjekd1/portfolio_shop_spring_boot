package com.example.demo.wishlist.service;

import com.example.demo.wishlist.entity.Wishlist;
import com.example.demo.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseWishlistService implements WishlistService {

    private final WishlistRepository wishlistRepository;

    @Override
    public List<Wishlist> findAll(String loginId) {
        return wishlistRepository.findByLoginIdOrderByCreatedDesc(loginId);
    }

    @Override
    public void add(String loginId, Integer itemId) {
        if (wishlistRepository.findByLoginIdAndItemId(loginId, itemId).isPresent()) {
            return;
        }
        wishlistRepository.save(new Wishlist(loginId, itemId));
    }

    @Override
    public void remove(String loginId, Integer itemId) {
        wishlistRepository.deleteByLoginIdAndItemId(loginId, itemId);
    }
}
