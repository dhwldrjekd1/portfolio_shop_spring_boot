package com.example.demo.common.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;

import java.util.Map;

// 세션 기반 로그인/권한 확인 유틸 (컨트롤러에서 관리자 전용, 본인 소유 여부를 검증할 때 사용)
public class SessionAuth {

    private static final String LOGIN_ID_KEY = "loginId";
    private static final String ROLE_KEY = "role";

    public static void login(HttpServletRequest request, String loginId, String role) {
        HttpSession session = request.getSession(true);
        session.setAttribute(LOGIN_ID_KEY, loginId);
        session.setAttribute(ROLE_KEY, role != null ? role : "user");
    }

    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
    }

    public static String currentLoginId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null ? (String) session.getAttribute(LOGIN_ID_KEY) : null;
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return currentLoginId(request) != null;
    }

    public static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && "admin".equals(session.getAttribute(ROLE_KEY));
    }

    public static boolean isSelfOrAdmin(HttpServletRequest request, String loginId) {
        String me = currentLoginId(request);
        return me != null && (me.equals(loginId) || isAdmin(request));
    }

    public static ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
    }

    public static ResponseEntity<?> forbidden() {
        return ResponseEntity.status(403).body(Map.of("success", false, "message", "권한이 없습니다."));
    }
}
