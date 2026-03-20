package com.example.demo.account.controller;

import com.example.demo.account.dto.AccountJoinRequest;
import com.example.demo.account.dto.AccountLoginRequest;
import com.example.demo.account.helper.AccountHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController //JSON 컨트롤러 임을 선언
@RequiredArgsConstructor //빈을 간편하게 생성자를 주입할수 있게해주는 애너테이션
@RequestMapping("/v1") //모든 HTTP 매서드의 요청을 매핑하기 위한 애너테이션
public class AccountController {
    private final AccountHelper accountHelper;

    //회원가입을 처리하는 매서드
    @PostMapping("/api/account/join")
    public ResponseEntity<?> join(@RequestBody AccountJoinRequest joinReq) {
        //입력값이 비어있다면 ?
        if (!StringUtils.hasLength(joinReq.getName()) ||
                !StringUtils.hasLength(joinReq.getLoginId()) ||
                !StringUtils.hasLength(joinReq.getLoginPw()) ||
                !StringUtils.hasLength(joinReq.getEmail()) ||
                !StringUtils.hasLength(joinReq.getPhone())) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        accountHelper.join(joinReq);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    //로그인을 처리하는 매서드
    @PostMapping("/api/account/login")
    public ResponseEntity<?> login(HttpServletRequest req, HttpServletResponse res, @RequestBody
    AccountLoginRequest loginReq) {
        if (!StringUtils.hasLength(loginReq.getLoginId()) ||
                !StringUtils.hasLength(loginReq.getLoginPw())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String output = accountHelper.login(loginReq, req, res);

        if (output == null) { //로그인 실패시
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(output, HttpStatus.OK);
    }
    //로그인 여부를 조회하는 매서드
    @GetMapping("/api/account/check")
    public ResponseEntity<?> check(HttpServletRequest req) {
        return new ResponseEntity<>(accountHelper.isLoggedIn(req), HttpStatus.OK);
    }
    //로그아웃을 처리하는 매서드
    @PostMapping("/api/account/logout")
    public ResponseEntity<?> logout(HttpServletRequest req, HttpServletResponse res) {
        accountHelper.logout(req, res);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

