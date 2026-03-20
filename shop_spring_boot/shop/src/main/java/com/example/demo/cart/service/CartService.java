package com.example.demo.cart.service;


import com.example.demo.cart.dto.CartRead;
import com.example.demo.cart.dto.CartRequest;


import java.util.List;

public interface CartService {

    //장바구니 상품 데이터 조회(특정 회원)
    List<CartRead> findAll(Integer memberId);

    //장바구니 상품 데이터 조회(특정 회원의 특정 상품 및 수량)
    CartRead find(Integer memberId, Integer productId, Integer quantity);

    //장바구니 상품 테이터 전체 삭제(특정 회원)
    void removeAll(Integer memberId);

    //장바구니 상품 데이터 삭제(특정 회원의 특정 상품 및 수량)
    void remove(Integer memberId, Integer productId, Integer quantity);

    //장바구니 데이터 저장(특정 회원의 특정 상품 및 수량)
    //Entity를 외부에 직접노출하는것은 보안상 좋지않음
    //CartRequest로 받는것이 보안상안전함으로 CartRequest를 사용
    void save(Integer memberId, CartRequest cartRequest);
}
