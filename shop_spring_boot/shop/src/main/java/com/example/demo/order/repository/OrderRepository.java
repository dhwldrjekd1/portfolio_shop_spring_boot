package com.example.demo.order.repository;

import com.example.demo.order.entity.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByLoginIdOrderByCreatedDesc(String loginId);
    List<Order> findAllByOrderByCreatedDesc();

    // 상태 변경 시 동시 요청(예: 취소 중복 클릭)으로 인한 재고 이중 복구를 막기 위한 행 잠금 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Integer id);
}