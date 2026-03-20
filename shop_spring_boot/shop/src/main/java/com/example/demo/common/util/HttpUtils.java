package com.example.demo.common.util;

import jakarta.servlet.http.HttpServletRequest;

public class HttpUtils {
    // 세션 입력
    public static void setSession(HttpServletRequest req, String key, Object value) {
        req.getSession().getAttribute(key);
    }

    // 세션 값 조회
    public static Object getSessionValue(HttpServletRequest req, String key) {
        return req.getSession().getAttribute(key);
    }

    // 세션 삭제
    public static void removeSession(HttpServletRequest req, String key) {
        req.getSession().removeAttribute(key);
    }
}
