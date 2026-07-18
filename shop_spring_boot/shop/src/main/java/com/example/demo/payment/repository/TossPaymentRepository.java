package com.example.demo.payment.repository;

import com.example.demo.payment.entity.TossPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface TossPaymentRepository extends JpaRepository<TossPayment, Integer> {
    Optional<TossPayment> findByTossOrderId(String tossOrderId);

    // used=false일 때만 원자적으로 true로 변경. 반환값이 0이면 이미 사용된 결제(동시 요청 등으로 인한 이중 사용 방지).
    @Modifying
    @Query("UPDATE TossPayment t SET t.used = true WHERE t.tossOrderId = :tossOrderId AND t.used = false")
    int markUsedIfUnused(@Param("tossOrderId") String tossOrderId);
}
