package com.example.demo.member.service;

import com.example.demo.common.validation.PasswordPolicy;
import com.example.demo.member.entity.Member;
import com.example.demo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service // 서비스임을 선언
@RequiredArgsConstructor // 자동으로 생성자 주입
public class BaseMemberService implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화
    private final JavaMailSender mailSender;

    // 임시 비밀번호 생성용
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGIT_CHARS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*";

    // 회원 데이터 저장
    @Override
    public void save(String name, String loginId, String loginPw, String email, String phone, String address, String address_detail, String zipcode) {
        PasswordPolicy.validate(loginPw);
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
        // 비밀번호 변경 시 정책 검증 후 암호화
        if (body.get("loginPw") != null && !body.get("loginPw").isEmpty()) {
            PasswordPolicy.validate(body.get("loginPw"));
            body = new java.util.HashMap<>(body);
            body.put("loginPw", passwordEncoder.encode(body.get("loginPw")));
        }
        member.update(body);
        memberRepository.save(member);
    }

    // 비밀번호 찾기 (임시 비밀번호 발급, 이메일로만 전달 — API 응답에는 노출하지 않음)
    @Override
    public void findPw(String loginId, String email) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("아이디 또는 이메일이 일치하지 않습니다."));
        if (!member.getEmail().equals(email)) {
            throw new RuntimeException("아이디 또는 이메일이 일치하지 않습니다.");
        }
        // 임시 비밀번호 생성 - 사이트 자체 비밀번호 정책(영문 소문자/숫자/특수문자 포함, 8자 이상)을 항상 만족하도록 생성
        String tempPw = generateTempPassword();
        // 임시 비밀번호 암호화 후 저장
        Map<String, String> body = Map.of("loginPw", passwordEncoder.encode(tempPw));
        member.update(body);
        memberRepository.save(member);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[Gentle Monster] 임시 비밀번호 안내");
        message.setText("요청하신 임시 비밀번호는 [" + tempPw + "] 입니다. 로그인 후 반드시 비밀번호를 변경해주세요.");
        mailSender.send(message);
    }

    // 영문 소문자/숫자/특수문자를 각각 최소 1개 포함하도록 보장하는 임시 비밀번호 생성 (PasswordPolicy와 항상 일치)
    private String generateTempPassword() {
        List<Character> chars = new ArrayList<>();
        chars.add(LOWERCASE_CHARS.charAt(RANDOM.nextInt(LOWERCASE_CHARS.length())));
        chars.add(DIGIT_CHARS.charAt(RANDOM.nextInt(DIGIT_CHARS.length())));
        chars.add(SPECIAL_CHARS.charAt(RANDOM.nextInt(SPECIAL_CHARS.length())));

        String pool = LOWERCASE_CHARS + DIGIT_CHARS + SPECIAL_CHARS;
        for (int i = 0; i < 7; i++) {
            chars.add(pool.charAt(RANDOM.nextInt(pool.length())));
        }
        Collections.shuffle(chars, RANDOM);

        StringBuilder sb = new StringBuilder();
        chars.forEach(sb::append);
        return sb.toString();
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

    private static final Set<String> VALID_ROLES = Set.of("user", "admin", "banned");

    // role 변경 (관리자)
    @Override
    public void updateRole(String loginId, String role) {
        if (!VALID_ROLES.contains(role)) {
            throw new IllegalArgumentException("role은 user, admin, banned 중 하나여야 합니다.");
        }
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
        } else {
            grade = "브론즈";
        }
        member.updateGrade(grade);
        memberRepository.save(member);
    }
}