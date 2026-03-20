package com.example.demo.cart.service;

import com.example.demo.cart.Repository.CartRepository;
import com.example.demo.cart.dto.CartRead;
import com.example.demo.cart.dto.CartRequest;
import com.example.demo.cart.entity.CartEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service //서비스임을 선언
@RequiredArgsConstructor //자동생성자 주입
public class BaseCartService implements CartService {

    private final CartRepository cartRepository;

    //장바구니 상품 데이터 목록 조회(특정 회원)
    @Override
    public List<CartRead> findAll(Integer memberId) {
        //리스트의 값들을 DTO로 변환해서 리턴
        return cartRepository.findAllByMemberId(memberId).stream().map(CartEntity::toRead).toList();
    }

    //장바구니 상품 데이터 조회(특정 회원의 특정 상품 및 수량)
    @Override
    public CartRead find(Integer memberId, Integer productId, Integer quantity) {
        Optional<CartEntity> cartOptional = cartRepository.findByMemberIdAndProductIdAndQuantity(memberId, productId, quantity);
        //값이 있으면 DTO로 변환해서 리턴, 없으면 null 리턴
        return cartOptional.map(CartEntity::toRead).orElse(null);
    }

    //장바구니 상품 데이터 전체 삭제(특정 회원)
    @Override
    @Transactional
    public void removeAll(Integer memberId) {
        cartRepository.deleteByMemberId(memberId);
    }

    //장바구니 상품 데이터 삭제(특정 회원의 특정 상품 수량)
    @Override
    @Transactional
    public void remove(Integer memberId, Integer productId, Integer quantity) {
        cartRepository.deleteByMemberIdAndProductIdAndQuantity(memberId, productId, quantity);
    }


    //장바구니 데이터 저장(특정 회원의 특정 상품)
    public void save(Integer memberId, CartRequest cartRequest) {
        cartRepository.save(cartRequest.toEntity(memberId));
    }

}
