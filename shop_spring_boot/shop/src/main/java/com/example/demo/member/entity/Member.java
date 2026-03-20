package com.example.demo.member.entity;


import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity //엔티티 선언
@Getter //게터를 자동으로 생성
@Table(name = "members") //데이터베이스의 테이블을 나타내는 애너테이션
public class Member {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 50, nullable = false, unique = true)
    private String loginId;

    @Column(length = 255, nullable = false)
    private String loginPw; //비밀번호는 String (암호화된 문자열 저장)

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 20)
    private String phone; //전화번호는 String ("010-1234-5678" 형식)

    @Column(length = 255)
    private String address;

    @Column(length = 255)
    private String address_detail;

    @Column(length = 10)
    private String zipcode;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created; //날짜/시간은 LocalDateTime

    public Member() {
    }

    public Member(String name, String loginId,String loginPw,String email,String phone,String address, String address_detail, String zipcode) {

        this.name = name;
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.address_detail = address_detail;
        this.zipcode = zipcode;
    }
}
