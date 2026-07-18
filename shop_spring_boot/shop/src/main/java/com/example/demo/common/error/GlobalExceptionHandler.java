package com.example.demo.common.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

// 컨트롤러의 try/catch(ApiError.badRequest)를 거치지 않고 새어나온 예외를 잡는 최종 안전망.
// 컨트롤러별 try/catch를 대체하지 않는다 - 누락되었거나 새로 추가된 엔드포인트에서
// 예외가 그대로 노출되지 않도록 응답 형식과 정보 노출 수준을 일관되게 맞추는 역할.
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // .orElseThrow() 등에서 발생하는 "대상 없음" - 404로 응답
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false, "message", "요청한 데이터를 찾을 수 없습니다."));
    }

    // 서비스 계층에서 의도적으로 던지는 업무 예외 - 메시지를 그대로 안내해도 안전한 경우
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<?> handleBadRequest(RuntimeException e) {
        String message = e.getMessage() != null ? e.getMessage() : "잘못된 요청입니다.";
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", message));
    }

    // DB/JPA 계층 예외 - SQL, 테이블/컬럼명 등 내부 정보는 로그로만 남기고 일반 메시지로 응답
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDataAccess(DataAccessException e) {
        log.error("DB 처리 중 오류", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요."));
    }

    // 그 외 예상하지 못한 예외 - 원문 메시지(내부 클래스/필드명 등)를 클라이언트에 노출하지 않음
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnexpected(Exception e) {
        log.error("처리되지 않은 예외 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요."));
    }
}
