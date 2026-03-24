package com.example.demo.member.service;

import com.example.demo.member.entity.Member;
import com.example.demo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service // 서비스임을 선언
@RequiredArgsConstructor // 자동으로 생성자 주입
public class BaseMemberService implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화

    // 회원 데이터 저장
    @Override
    public void save(String name, String loginId, String loginPw, String email, String phone, String address, String address_detail, String zipcode) {
        // 비밀번호 암호화 후 저장
        String encodedPw = passwordEncoder.encode(loginPw);
        Member member = new Member(name, loginId, encodedPw, email, phone, address, address_detail, zipcode);
        memberRepository.save(member);
    }

    // 회원 데이터 조회 (로그인)
    @Override
    public Member find(String loginId, String loginPw) {
        Optional<Member> memberOptional = memberRepository.findByLoginId(loginId);
        if (memberOptional.isEmpty()) return null;
        Member member = memberOptional.get();
        // 강퇴된 회원 로그인 차단
        if ("banned".equals(member.getRole())) return null;
        // 암호화된 비밀번호 비교
        if (!passwordEncoder.matches(loginPw, member.getLoginPw())) return null;
        return member;
    }

    @Override
    public Member findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId).orElseThrow();
    }

    @Override
    public void update(String loginId, Map<String, String> body) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        // 비밀번호 변경 시 암호화
        if (body.get("loginPw") != null && !body.get("loginPw").isEmpty()) {
            body = new java.util.HashMap<>(body);
            body.put("loginPw", passwordEncoder.encode(body.get("loginPw")));
        }
        member.update(body);
        memberRepository.save(member);
    }

    // 비밀번호 찾기 (임시 비밀번호 발급)
    @Override
    public String findPw(String loginId, String email) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("아이디 또는 이메일이 일치하지 않습니다."));
        if (!member.getEmail().equals(email)) {
            throw new RuntimeException("아이디 또는 이메일이 일치하지 않습니다.");
        }
        // 임시 비밀번호 생성 (영문+숫자 8자리)
        String tempPw = java.util.UUID.randomUUID().toString().substring(0, 8);
        // 임시 비밀번호 암호화 후 저장
        Map<String, String> body = Map.of("loginPw", passwordEncoder.encode(tempPw));
        member.update(body);
        memberRepository.save(member);
        return tempPw;
    }

    // 회원목록 (관리자)
    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    // 회원탈퇴
    @Override
    public void delete(String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        memberRepository.delete(member);
    }

    // 등급 변경 (관리자 수동)
    @Override
    public void updateGrade(String loginId, String grade) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        member.updateGrade(grade);
        memberRepository.save(member);
    }

    // role 변경 (관리자)
    @Override
    public void updateRole(String loginId, String role) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        member.updateRole(role);
        memberRepository.save(member);
    }

    // 구매금액에 따른 자동 등급 업데이트
    @Override
    public void updateGradeByAmount(String loginId, Integer totalAmount) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        String grade;
        if (totalAmount >= 2000000) {
            grade = "플래티넘";
        } else if (totalAmount >= 1500000) {
            grade = "골드";
        } else if (totalAmount >= 1000000) {
            grade = "실버";
        } else if (totalAmount >= 500000) {
            grade = "브론즈";
        } else {
            grade = "브론즈";
        }
        member.updateGrade(grade);
        memberRepository.save(member);
    }
}