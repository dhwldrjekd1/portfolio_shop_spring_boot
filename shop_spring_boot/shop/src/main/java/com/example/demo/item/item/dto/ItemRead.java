package com.example.demo.item.item.dto;

import lombok.Builder;
import lombok.Getter;

@Getter  //모든 필드의 게터를 자동으로 생성해주는 애너테이션
@Builder // 필드의 값들을 간편하게 초기화하고 인스턴스를 생성할수 있게 해주는 애너테이션
public class ItemRead {

    private Integer id; //상품의 아이디 필드
    private String name; //상품명 필드
    private String imgPath; //상품의 사진 경로 필드
    private Integer price; //상품가격 필드
    private Integer discountPer; // 상품의 할인율 필드
}
