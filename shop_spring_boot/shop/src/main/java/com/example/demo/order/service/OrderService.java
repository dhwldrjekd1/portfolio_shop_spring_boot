package com.example.demo.order.service;

import com.example.demo.order.entity.Order;
import java.util.List;
import java.util.Map;

public interface OrderService {
    Order save(String loginId, String name, String address, String payment, String cardNumber, Integer amount, List<Map<String, Object>> items);
    List<Order> findAll(String loginId);
    List<Order> findAll();
    void updateStatus(Integer id, String status);
    void delete(Integer id); // 주문 삭제 (관리자)
}