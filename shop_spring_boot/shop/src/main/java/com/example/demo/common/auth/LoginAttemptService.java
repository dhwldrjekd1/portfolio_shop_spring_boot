package com.example.demo.common.auth;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 로그인 brute-force 방어: (계정 loginId + 요청 IP) 조합당 연속 실패 횟수를 추적해 일시 잠금
// loginId만으로 키를 잡으면 공격자가 남의 loginId로 틀린 비밀번호를 반복 전송해 정작 계정 주인을
// 잠가버릴 수 있어, IP를 조합해 공격자의 IP만 잠기고 계정 주인의 정상 접속은 막히지 않게 함
@Component
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MS = 5 * 60 * 1000; // 5분

    private static class Attempt {
        int count = 0;
        long lockedUntil = 0;
    }

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    private String key(String loginId, String ip) {
        return loginId + "|" + ip;
    }

    public boolean isLocked(String loginId, String ip) {
        if (loginId == null) return false;
        Attempt a = attempts.get(key(loginId, ip));
        if (a == null) return false;
        synchronized (a) {
            if (a.lockedUntil == 0) return false;
            if (System.currentTimeMillis() >= a.lockedUntil) {
                attempts.remove(key(loginId, ip));
                return false;
            }
            return true;
        }
    }

    public void loginFailed(String loginId, String ip) {
        if (loginId == null) return;
        Attempt a = attempts.computeIfAbsent(key(loginId, ip), k -> new Attempt());
        synchronized (a) {
            a.count++;
            if (a.count >= MAX_ATTEMPTS) {
                a.lockedUntil = System.currentTimeMillis() + LOCK_DURATION_MS;
            }
        }
    }

    public void loginSucceeded(String loginId, String ip) {
        if (loginId == null) return;
        attempts.remove(key(loginId, ip));
    }

    public long remainingLockSeconds(String loginId, String ip) {
        Attempt a = attempts.get(key(loginId, ip));
        if (a == null || a.lockedUntil == 0) return 0;
        return Math.max(0, (a.lockedUntil - System.currentTimeMillis()) / 1000);
    }
}
