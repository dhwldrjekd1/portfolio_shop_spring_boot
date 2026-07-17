package com.example.demo.common.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;

import java.util.Map;

// 예외의 원문 메시지를 그대로 클라이언트에 노출하지 않기 위한 공통 에러 응답 헬퍼.
// DB/JPA 계층 예외(SQL, 테이블/컬럼명 등 내부 정보 포함)는 서버 로그로만 남기고 일반 메시지로 응답한다.
@Slf4j
public class ApiError {

    public static ResponseEntity<?> badRequest(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", safeMessage(e)));
    }

    private static String safeMessage(Exception e) {
        if (e instanceof DataAccessException) {
            log.error("DB 처리 중 오류", e);
            return "요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요.";
        }
        String message = e.getMessage();
        return message != null ? message : "요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요.";
    }
}
