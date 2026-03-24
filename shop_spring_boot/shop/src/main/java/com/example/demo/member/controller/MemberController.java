package com.example.demo.member.controller;
import com.example.demo.member.entity.Member;
import com.example.demo.member.service.MemberService;
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
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        Member member = memberService.find(body.get("loginId"), body.get("loginPw"));
        if (member != null) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "name", member.getName(),
                    "email", member.getEmail(),
                    "loginId", member.getLoginId(),
                    "role", member.getRole() != null ? member.getRole() : "user"
            ));
        }
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", "아이디 또는 비밀번호가 틀렸습니다."));
    }
    // 회원정보 조회
    @GetMapping("/info/{loginId}")
    public ResponseEntity<?> info(@PathVariable String loginId) {
        try {
            Member member = memberService.findByLoginId(loginId);
            return ResponseEntity.ok(member);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    // 회원정보 수정
    @PutMapping("/update/{loginId}")
    public ResponseEntity<?> update(@PathVariable String loginId, @RequestBody Map<String, String> body) {
        try {
            memberService.update(loginId, body);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 비밀번호 찾기
    @PostMapping("/find-pw")
    public ResponseEntity<?> findPw(@RequestBody Map<String, String> body) {
        try {
            String loginId = body.get("loginId");
            String email = body.get("email");
            String tempPw = memberService.findPw(loginId, email);
            return ResponseEntity.ok(Map.of("success", true, "tempPw", tempPw));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    // 회원 목록 (관리자)
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        try {
            List<Member> members = memberService.findAll();
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 회원탈퇴
    @DeleteMapping("/delete/{loginId}")
    public ResponseEntity<?> delete(@PathVariable String loginId) {
        try {
            memberService.delete(loginId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 등급 변경 (관리자 수동)
    @PutMapping("/grade/{loginId}")
    public ResponseEntity<?> updateGrade(@PathVariable String loginId, @RequestBody Map<String, String> body) {
        try {
            memberService.updateGrade(loginId, body.get("grade"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // role 변경 (관리자 - 강퇴/권한변경)
    @PutMapping("/role/{loginId}")
    public ResponseEntity<?> updateRole(@PathVariable String loginId, @RequestBody Map<String, String> body) {
        try {
            memberService.updateRole(loginId, body.get("role"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

}