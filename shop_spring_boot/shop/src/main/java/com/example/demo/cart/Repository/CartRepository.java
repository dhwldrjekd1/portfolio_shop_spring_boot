package com.example.demo.cart.Repository;

import com.example.demo.cart.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Integer> {

    //장바구니 목록 조회(특정 회원)
    List<CartEntity> findAllByMemberId(Integer memberId);

    //장바구니 정보 조회(특정 회원 및 특정 상품)
    Optional<CartEntity> findByMemberIdAndProductId(Integer memberId, Integer productId);

    //장바구니 정보 조회(특정 회원 및 특정 상품 및 특정 수량)
    Optional<CartEntity> findByMemberIdAndProductIdAndQuantity(Integer memberId, Integer productId, Integer quantity);

    //장바구니 삭제(특정 회원)
    void deleteByMemberId(Integer memberId);

    //장바구니 삭제(특정 회원 및 특정 상품)
    void deleteByMemberIdAndProductId(Integer memberId, Integer productId);

    //장바구니 삭제(특정 회원 및 특정 상품 및 특정 수량)
    void deleteByMemberIdAndProductIdAndQuantity(Integer memberId, Integer productId, Integer quantity);
}
