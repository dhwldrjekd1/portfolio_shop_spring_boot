package com.example.demo.member.service;

import com.example.demo.member.entity.Member;

public interface MemberService {

    // 회원 데이터 저장
    void save(String name, String loginId, String loginPw, String email, String phone, String address, String address_detail, String zipcode);

    // 회원 데이터 조회
    Member find(String loginId, String loginPw);
}
