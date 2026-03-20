package com.example.demo.order.dto;

import com.example.demo.order.entity.OrderEntity;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class OrderRequest {

    private String name;
    private String address;
    private String payment;
    private String cardNumber;
    private Long amount;
    private List<Integer> itemIds;

    //엔티티 객체로 변환
    public OrderEntity toEntity(Integer memberId) {
        return new OrderEntity(memberId, name, address, payment, cardNumber, amount);
    }
}
