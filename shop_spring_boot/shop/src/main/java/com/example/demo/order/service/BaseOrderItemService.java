package com.example.demo.order.service;

import com.example.demo.order.entity.OrderItemEntity;
import com.example.demo.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseOrderItemService implements OrderItemService{

    private final OrderItemRepository orderItemRepository;

    //주문 목록 조회
    @Override
    public List<OrderItemEntity> findAll(Integer orderId) {
        return orderItemRepository.findAllByOrderId(orderId);
    }


    //주문 상품 데이터 저장
    @Override
    public void saveAll(List<OrderItemEntity> orderItems) {
        orderItemRepository.saveAll(orderItems);
    }

}
