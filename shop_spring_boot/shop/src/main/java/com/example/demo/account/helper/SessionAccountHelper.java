package com.example.demo.account.helper;

import com.example.demo.account.dto.AccountJoinRequest;
import com.example.demo.account.dto.AccountLoginRequest;
import com.example.demo.account.etc.AccountConstants;
import com.example.demo.common.util.HttpUtils;
import com.example.demo.member.entity.Member;
import com.example.demo.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionAccountHelper implements AccountHelper {

    private final MemberService memberService;

    //회원가입
    @Override
    public void join(AccountJoinRequest joinReq) {
        memberService.save(joinReq.getName(), joinReq.getLoginId(), joinReq.getLoginPw(), joinReq.getEmail(), joinReq.getPhone(), joinReq.getAddress(), joinReq.getAddress_detail(), joinReq.getZipcode());
    }

    // 로그인
    @Override
    public String login(AccountLoginRequest loginReq, HttpServletRequest req, HttpServletResponse res) {
        Member member = memberService.find(loginReq.getLoginId(), loginReq.getLoginPw());

        //회원 데이터가 없으면
        if (member == null) {
            return null;
        }

        HttpUtils.setSession(req, AccountConstants.MEMBER_ID_NAME, member.getId());
        return member.getLoginId();
    }

    //회원 아이디 조회
    @Override
    public Integer getMemberId(HttpServletRequest req) {

        Object memberId = HttpUtils.getSessionValue(req, AccountConstants.MEMBER_ID_NAME);
        if (memberId != null) {
            return (int) memberId;
        }
        return null;
    }

    //로그인 여부 확인
    @Override
    public boolean isLoggedIn(HttpServletRequest req) {
        return getMemberId(req) != null;
    }

    //로그아웃
    @Override
    public void logout(HttpServletRequest req, HttpServletResponse res) {
        HttpUtils.removeSession(req, AccountConstants.MEMBER_ID_NAME);
    }
}