package com.example.demo.order.repository;

import com.example.demo.order.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Integer> {

    //주문 상품 목록 조회
    List<OrderItemEntity> findAllByOrderId(Integer orderId);
}
