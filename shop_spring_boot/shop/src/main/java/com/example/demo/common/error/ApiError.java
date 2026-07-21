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
        // 컨트롤러들이 요청 바디(Map<String, Object>)를 Integer 등으로 강제 캐스팅하는 곳이 많은데,
        // 예상과 다른 타입(예: JSON 숫자가 커서 Long으로 역직렬화됨)이 오면 ClassCastException/
        // NullPointerException처럼 클래스명 등 내부 구현이 드러나는 예외가 던져질 수 있다.
        // 이런 종류는 의도적으로 던지는 곳이 없으므로(항상 사고성 예외) 일반 메시지로 치환한다.
        if (e instanceof ClassCastException || e instanceof NullPointerException) {
            log.error("요청 파싱 중 오류", e);
            return "요청 형식이 올바르지 않습니다.";
        }
        String message = e.getMessage();
        return message != null ? message : "요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요.";
    }
}
