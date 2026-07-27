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

    // 구매 여부 확인 전용 - 주문/주문상품 전체를 불러오지 않고 EXISTS 쿼리 한 번으로 처리
    boolean existsByLoginIdAndStatusAndOrderItems_ItemId(String loginId, String status, Integer itemId);

    // 상태 변경 시 동시 요청(예: 취소 중복 클릭)으로 인한 재고 이중 복구를 막기 위한 행 잠금 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Integer id);
}