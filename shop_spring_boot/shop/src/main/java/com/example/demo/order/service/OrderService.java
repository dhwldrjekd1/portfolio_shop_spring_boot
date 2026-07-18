package com.example.demo.order.service;

import com.example.demo.order.entity.Order;
import java.util.List;
import java.util.Map;

public interface OrderService {
    // tossOrderId: payment가 "toss"일 때, 결제 승인 시 사용된 주문번호. 승인 기록과 대조해 결제 없이 주문이 생성되는 것을 막는다.
    Order save(String loginId, String name, String address, String payment, String cardNumber, Integer amount, List<Map<String, Object>> items, String tossOrderId);
    List<Order> findAll(String loginId);
    List<Order> findAll();
    Order findById(Integer id);
    void updateStatus(Integer id, String status);
    void delete(Integer id); // 주문 삭제 (관리자)
}