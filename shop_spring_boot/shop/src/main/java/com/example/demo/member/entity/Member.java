package com.example.demo.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Map;

@Entity // 엔티티 선언
@Getter // 게터를 자동으로 생성
@Table(name = "members") // 데이터베이스의 테이블을 나타내는 애너테이션
public class Member {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 50, nullable = false, unique = true)
    private String loginId;

    @Column(length = 255, nullable = false)
    private String loginPw; // 비밀번호는 암호화된 문자열 저장

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 20)
    private String phone; // 전화번호 ("010-1234-5678" 형식)

    @Column(length = 255)
    private String address;

    @Column(name = "address_detail", length = 255)
    private String addressDetail;

    @Column(length = 10)
    private String zipcode;

    @Column(length = 20)
    private String role; // user / admin

    @Column(length = 20)
    private String grade; // 브론즈 / 실버 / 골드 / 플래티넘

    @Column(nullable = false, updatable = false)
    private LocalDateTime created; // 가입일시

    public Member() {}

    public Member(String name, String loginId, String loginPw, String email, String phone, String address, String addressDetail, String zipcode) {
        this.name = name;
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.addressDetail = addressDetail;
        this.zipcode = zipcode;
        this.created = LocalDateTime.now();
        this.role = "user";
        this.grade = "브론즈"; // 기본 등급
    }

    // 회원정보 수정
    public void update(Map<String, String> body) {
        if (body.get("name") != null) this.name = body.get("name");
        if (body.get("email") != null) this.email = body.get("email");
        if (body.get("phone") != null) this.phone = body.get("phone");
        if (body.get("zipcode") != null) this.zipcode = body.get("zipcode");
        if (body.get("address") != null) this.address = body.get("address");
        if (body.get("address_detail") != null) this.addressDetail = body.get("address_detail");
        if (body.get("loginPw") != null && !body.get("loginPw").isEmpty()) this.loginPw = body.get("loginPw");
    }

    // 등급 수정 (관리자 수동 변경 or 자동 업데이트)
    public void updateGrade(String grade) {
        this.grade = grade;
    }

    // role 변경 (관리자)
    public void updateRole(String role) {
        this.role = role;
    }
}