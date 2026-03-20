package com.example.demo.item.item.repository;

import com.example.demo.item.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Integer> {

    //여러 아이디로 상품을 조회
    List<ItemEntity> findAllByIdIn(List<Integer> ids);
}
