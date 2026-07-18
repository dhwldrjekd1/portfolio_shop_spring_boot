package com.example.demo.payment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

// 토스페이먼츠에서 승인 완료된 결제 기록. 주문(Order) 생성 시 이 기록과 대조해
// "결제 없이 주문 생성" 또는 "동일 결제로 여러 주문 생성"을 막는 데 사용한다.
@Entity
@Getter
@Table(name = "toss_payments")
public class TossPayment {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false, unique = true)
    private String tossOrderId;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private boolean used;

    @Column(nullable = false)
    private LocalDateTime confirmedAt;

    public TossPayment() {}

    public TossPayment(String tossOrderId, String paymentKey, Integer amount) {
        this.tossOrderId = tossOrderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.used = false;
        this.confirmedAt = LocalDateTime.now();
    }

}
