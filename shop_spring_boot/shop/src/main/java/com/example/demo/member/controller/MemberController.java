package com.example.demo.member.controller;
import com.example.demo.common.error.ApiError;
import com.example.demo.common.auth.LoginAttemptService;
import com.example.demo.common.auth.SessionAuth;
import com.example.demo.member.entity.Member;
import com.example.demo.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final LoginAttemptService loginAttemptService;
    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            memberService.save(
                    body.get("name"),
                    body.get("loginId"),
                    body.get("loginPw"),
                    body.get("email"),
                    body.get("phone"),
                    body.get("address"),
                    body.get("address_detail"),
                    body.get("zipcode")
            );
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String loginId = body.get("loginId");

        if (loginAttemptService.isLocked(loginId)) {
            long sec = loginAttemptService.remainingLockSeconds(loginId);
            return ResponseEntity.status(429).body(Map.of(
                    "success", false,
                    "message", "로그인 시도가 너무 많습니다. " + sec + "초 후 다시 시도해주세요."
            ));
        }

        Member member = memberService.find(loginId, body.get("loginPw"));
        if (member != null) {
            loginAttemptService.loginSucceeded(loginId);
            String role = member.getRole() != null ? member.getRole() : "user";
            SessionAuth.login(request, member.getLoginId(), role);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "name", member.getName(),
                    "email", member.getEmail(),
                    "loginId", member.getLoginId(),
                    "role", role
            ));
        }
        loginAttemptService.loginFailed(loginId);
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", "아이디 또는 비밀번호가 틀렸습니다."));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        SessionAuth.logout(request);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 회원정보 조회 (본인 또는 관리자)
    @GetMapping("/info/{loginId}")
    public ResponseEntity<?> info(@PathVariable String loginId, HttpServletRequest request) {
        if (!SessionAuth.isSelfOrAdmin(request, loginId)) return SessionAuth.forbidden();
        try {
            Member member = memberService.findByLoginId(loginId);
            return ResponseEntity.ok(member);
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }
    // 회원정보 수정 (본인 또는 관리자)
    @PutMapping("/update/{loginId}")
    public ResponseEntity<?> update(@PathVariable String loginId, @RequestBody Map<String, String> body, HttpServletRequest request) {
        if (!SessionAuth.isSelfOrAdmin(request, loginId)) return SessionAuth.forbidden();
        try {
            memberService.update(loginId, body);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }

    // 비밀번호 찾기
    @PostMapping("/find-pw")
    public ResponseEntity<?> findPw(@RequestBody Map<String, String> body) {
        try {
            String loginId = body.get("loginId");
            String email = body.get("email");
            memberService.findPw(loginId, email);
            return ResponseEntity.ok(Map.of("success", true, "message", "입력하신 이메일로 임시 비밀번호를 발송했습니다."));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }
    // 회원 목록 (관리자)
    @GetMapping("/list")
    public ResponseEntity<?> list(HttpServletRequest request) {
        if (!SessionAuth.isAdmin(request)) return SessionAuth.forbidden();
        try {
            List<Member> members = memberService.findAll();
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }

    // 회원탈퇴 (본인 또는 관리자)
    @DeleteMapping("/delete/{loginId}")
    public ResponseEntity<?> delete(@PathVariable String loginId, HttpServletRequest request) {
        if (!SessionAuth.isSelfOrAdmin(request, loginId)) return SessionAuth.forbidden();
        try {
            memberService.delete(loginId);
            if (loginId.equals(SessionAuth.currentLoginId(request))) SessionAuth.logout(request);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }

    // 등급 변경 (관리자 수동)
    @PutMapping("/grade/{loginId}")
    public ResponseEntity<?> updateGrade(@PathVariable String loginId, @RequestBody Map<String, String> body, HttpServletRequest request) {
        if (!SessionAuth.isAdmin(request)) return SessionAuth.forbidden();
        try {
            memberService.updateGrade(loginId, body.get("grade"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }

    // role 변경 (관리자 - 강퇴/권한변경)
    @PutMapping("/role/{loginId}")
    public ResponseEntity<?> updateRole(@PathVariable String loginId, @RequestBody Map<String, String> body, HttpServletRequest request) {
        if (!SessionAuth.isAdmin(request)) return SessionAuth.forbidden();
        try {
            memberService.updateRole(loginId, body.get("role"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ApiError.badRequest(e);
        }
    }

}