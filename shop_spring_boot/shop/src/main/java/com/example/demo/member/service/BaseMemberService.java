package com.example.demo.member.service;

import com.example.demo.member.entity.Member;
import com.example.demo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service // 서비스임을 선언
@RequiredArgsConstructor //자동으로 생성자 주입
public class BaseMemberService implements MemberService {

    private final MemberRepository memberRepository;

    //회원 데이터 저장
    @Override
    public void save(String name, String loginId, String loginPw, String email, String phone, String address, String address_detail, String zipcode) {

    }

    //회원 데이터 조회
    @Override
    public Member find(String loginId, String loginPw) {

        Optional<Member> memberOptional = memberRepository.findByLoginIdAndLoginPw(loginId,loginPw);
        //회원 데이터가 있으면 해당 값 리턴(없으면 NULL 리턴)
        return memberOptional.orElse(null);
    }
}
