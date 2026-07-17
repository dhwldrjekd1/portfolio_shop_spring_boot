package com.example.demo.common.auth;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 로그인 brute-force 방어: 계정(loginId)당 연속 실패 횟수를 추적해 일시 잠금
@Component
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MS = 5 * 60 * 1000; // 5분

    private static class Attempt {
        int count = 0;
        long lockedUntil = 0;
    }

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public boolean isLocked(String loginId) {
        if (loginId == null) return false;
        Attempt a = attempts.get(loginId);
        if (a == null) return false;
        synchronized (a) {
            if (a.lockedUntil == 0) return false;
            if (System.currentTimeMillis() >= a.lockedUntil) {
                attempts.remove(loginId);
                return false;
            }
            return true;
        }
    }

    public void loginFailed(String loginId) {
        if (loginId == null) return;
        Attempt a = attempts.computeIfAbsent(loginId, k -> new Attempt());
        synchronized (a) {
            a.count++;
            if (a.count >= MAX_ATTEMPTS) {
                a.lockedUntil = System.currentTimeMillis() + LOCK_DURATION_MS;
            }
        }
    }

    public void loginSucceeded(String loginId) {
        if (loginId == null) return;
        attempts.remove(loginId);
    }

    public long remainingLockSeconds(String loginId) {
        Attempt a = attempts.get(loginId);
        if (a == null || a.lockedUntil == 0) return 0;
        return Math.max(0, (a.lockedUntil - System.currentTimeMillis()) / 1000);
    }
}
