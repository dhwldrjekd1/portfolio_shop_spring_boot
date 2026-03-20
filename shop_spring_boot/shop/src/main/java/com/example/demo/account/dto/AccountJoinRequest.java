package com.example.demo.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter //게터 선언
@Setter //세터 선언
public class AccountJoinRequest {

    private String name;  //회원명 필드
    private String loginId; //로그인아이디 필드
    private String loginPw; //로그인비밀번호 필드
    private String email; //회원정보 이메일 필드
    private String phone; //회원정보 핸드폰번호 필드
    private String address; //회원정보 주소 필드
    private String address_detail; // 회원정보 상세주소 필드
    private String zipcode; // 회원정보 우편번호 필드
}
