package com.example.demo.order.service;

import com.example.demo.order.entity.OrderItemEntity;
import java.util.List;

public interface OrderItemService {

    //주문 상품 목록 조회
    List<OrderItemEntity> findAll(Integer orderId);

    //주문 상품 데이터 저장
    void saveAll(List<OrderItemEntity> orderItems);
}
