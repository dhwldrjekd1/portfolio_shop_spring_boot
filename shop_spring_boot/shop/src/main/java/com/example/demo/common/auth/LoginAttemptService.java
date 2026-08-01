package com.example.demo.common.auth;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 로그인 brute-force 방어. 두 종류의 잠금을 함께 둔다:
// 1) (계정 loginId + 요청 IP) 조합당 5회 - loginId만으로 키를 잡으면 공격자가 남의 loginId로
//    틀린 비밀번호를 반복 전송해 정작 계정 주인을 잠가버릴 수 있어, IP를 조합해 공격자의 IP만
//    잠기고 계정 주인의 정상 접속은 막히지 않게 함
// 2) 계정 loginId 단독으로 더 높은 임계치(20회) - 1)만 있으면 여러 IP(봇넷 등)로 나눠서 공격할
//    때 IP당 5회씩 사실상 무제한으로 시도할 수 있어, 분산 공격에 대비해 계정 전체 상한도 둠
@Component
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int MAX_GLOBAL_ATTEMPTS = 20;
    private static final long LOCK_DURATION_MS = 5 * 60 * 1000; // 5분

    private static class Attempt {
        int count = 0;
        long lockedUntil = 0;
    }

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();       // key: loginId|ip
    private final Map<String, Attempt> globalAttempts = new ConcurrentHashMap<>(); // key: loginId

    private String key(String loginId, String ip) {
        return loginId + "|" + ip;
    }

    public boolean isLocked(String loginId, String ip) {
        if (loginId == null) return false;
        return isLockedIn(attempts, key(loginId, ip)) || isLockedIn(globalAttempts, loginId);
    }

    public void loginFailed(String loginId, String ip) {
        if (loginId == null) return;
        recordFailure(attempts, key(loginId, ip), MAX_ATTEMPTS);
        recordFailure(globalAttempts, loginId, MAX_GLOBAL_ATTEMPTS);
    }

    public void loginSucceeded(String loginId, String ip) {
        if (loginId == null) return;
        attempts.remove(key(loginId, ip));
        globalAttempts.remove(loginId);
    }

    public long remainingLockSeconds(String loginId, String ip) {
        return Math.max(
                remainingIn(attempts, key(loginId, ip)),
                remainingIn(globalAttempts, loginId));
    }

    private boolean isLockedIn(Map<String, Attempt> map, String mapKey) {
        Attempt a = map.get(mapKey);
        if (a == null) return false;
        synchronized (a) {
            if (a.lockedUntil == 0) return false;
            if (System.currentTimeMillis() >= a.lockedUntil) {
                map.remove(mapKey);
                return false;
            }
            return true;
        }
    }

    private void recordFailure(Map<String, Attempt> map, String mapKey, int maxAttempts) {
        Attempt a = map.computeIfAbsent(mapKey, k -> new Attempt());
        synchronized (a) {
            a.count++;
            if (a.count >= maxAttempts) {
                a.lockedUntil = System.currentTimeMillis() + LOCK_DURATION_MS;
            }
        }
    }

    private long remainingIn(Map<String, Attempt> map, String mapKey) {
        Attempt a = map.get(mapKey);
        if (a == null || a.lockedUntil == 0) return 0;
        return Math.max(0, (a.lockedUntil - System.currentTimeMillis()) / 1000);
    }
}
