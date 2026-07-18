package com.example.demo.item.repository;

import com.example.demo.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 상품 레포지터리
public interface ItemRepository extends JpaRepository<Item, Integer> {
    // 기본 CRUD는 JpaRepository에서 제공

    // 재고가 충분할 때만 원자적으로 차감. 반환값이 0이면 재고 부족(동시 주문 등으로 이미 소진).
    @Modifying
    @Query("UPDATE Item i SET i.stock = i.stock - :qty WHERE i.id = :id AND i.stock >= :qty")
    int decreaseStockIfAvailable(@Param("id") Integer id, @Param("qty") Integer qty);

    // 주문 취소 시 차감했던 재고를 원자적으로 복구
    @Modifying
    @Query("UPDATE Item i SET i.stock = i.stock + :qty WHERE i.id = :id")
    void increaseStock(@Param("id") Integer id, @Param("qty") Integer qty);
}