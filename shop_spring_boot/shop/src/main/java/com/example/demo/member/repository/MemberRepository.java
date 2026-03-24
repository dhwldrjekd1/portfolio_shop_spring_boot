package com.example.demo.member.repository;

import com.example.demo.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    
    // 아이디와 패스워드로 회원 정보를 조회
    Optional<Member> findByLoginIdAndLoginPw(String loginId, String loginPw);

    Optional<Member> findByLoginId(String loginId);
}
