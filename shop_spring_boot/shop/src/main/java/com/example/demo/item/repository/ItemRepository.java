package com.example.demo.item.repository;

import com.example.demo.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

// 상품 레포지터리
public interface ItemRepository extends JpaRepository<Item, Integer> {
    // 기본 CRUD는 JpaRepository에서 제공
}