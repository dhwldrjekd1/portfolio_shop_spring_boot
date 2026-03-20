package com.example.demo.cart.dto;

import com.example.demo.cart.entity.CartEntity;
import lombok.Getter;

@Getter
public class CartRequest {

    private Integer productId;
    private Integer quantity; //수량 추가

    //엔티티 객체로 변환
    //회원 아이디와 상품아이디 필드값을 인수로 사용하여 장바구니 엔티티객체를 생성하고 리턴 장바구니 서비스 데이터 저장
    public CartEntity toEntity(Integer memberId) {
        return new CartEntity(memberId, productId, quantity);
    }
}
