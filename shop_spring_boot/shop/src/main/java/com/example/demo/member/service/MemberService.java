package com.example.demo.member.service;

import com.example.demo.member.entity.Member;
import java.util.List;
import java.util.Map;

public interface MemberService {
    // 회원 데이터 저장
    void save(String name, String loginId, String loginPw, String email, String phone, String address, String address_detail, String zipcode);
    // 회원 데이터 조회
    Member find(String loginId, String loginPw);
    Member findByLoginId(String loginId);
    void update(String loginId, Map<String, String> body);
    // 회원목록 (관리자)
    List<Member> findAll();
    // 비밀번호 찾기
    String findPw(String loginId, String email);
    // 회원탈퇴
    void delete(String loginId);
    // 등급 변경
    void updateGrade(String loginId, String grade);
    // role 변경 (관리자)
    void updateRole(String loginId, String role);
    // 구매금액에 따른 자동 등급 업데이트
    void updateGradeByAmount(String loginId, Integer totalAmount);
}