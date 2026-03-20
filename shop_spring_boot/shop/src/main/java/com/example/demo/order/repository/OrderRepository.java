package com.example.demo.order.repository;

import com.example.demo.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {

    //주문 목록 조회
    List<OrderEntity> findAllByMemberIdOrderByIdDesc(Integer memberId);

    //주문 정보 조회(특정 아이디 및 특정 회원)
    Optional<OrderEntity> findByIdAndMemberId(Integer id, Integer memberId);
}
