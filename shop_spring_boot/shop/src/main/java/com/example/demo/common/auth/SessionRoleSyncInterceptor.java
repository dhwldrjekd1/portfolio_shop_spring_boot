package com.example.demo.common.auth;

import com.example.demo.member.entity.Member;
import com.example.demo.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;
import java.util.Optional;

// 매 요청마다 세션의 role을 DB 최신 상태와 동기화 — 관리자가 강퇴/권한변경을 해도
// 이미 로그인된 세션이 만료 전까지 예전 권한을 계속 쓰는 것을 막기 위함
@Component
@RequiredArgsConstructor
public class SessionRoleSyncInterceptor implements HandlerInterceptor {

    private final MemberRepository memberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        if (session == null) return true;

        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return true;

        Optional<Member> memberOptional = memberRepository.findByLoginId(loginId);
        if (memberOptional.isEmpty() || "banned".equals(memberOptional.get().getRole())) {
            session.invalidate();
            return true;
        }

        String currentRole = memberOptional.get().getRole();
        if (!Objects.equals(currentRole, session.getAttribute("role"))) {
            session.setAttribute("role", currentRole);
        }
        return true;
    }
}
