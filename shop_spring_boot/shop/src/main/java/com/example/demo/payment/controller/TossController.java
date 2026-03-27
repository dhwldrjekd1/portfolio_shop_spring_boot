package com.example.demo.payment.controller;

import com.example.demo.payment.TossConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class TossController {

    private final TossConfig tossConfig;

    // 클라이언트 키 반환 (Vue에서 토스 위젯 초기화 시 사용)
    @GetMapping("/client-key")
    public ResponseEntity<?> getClientKey() {
        return ResponseEntity.ok(Map.of("clientKey", tossConfig.getClientKey()));
    }

    // 결제 승인 (토스 → 서버 → 토스 API 호출)
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> body) {
        try {
            String paymentKey = (String) body.get("paymentKey");
            String orderId    = (String) body.get("orderId");
            Object amount     = body.get("amount");

            // 시크릿 키 Base64 인코딩 (토스 API 인증 방식)
            String secretKey  = tossConfig.getSecretKey() + ":";
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());

            // 토스 결제 승인 API 호출
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + encodedKey);

            Map<String, Object> requestBody = Map.of(
                    "paymentKey", paymentKey,
                    "orderId",    orderId,
                    "amount",     amount
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    TossConfig.CONFIRM_URL,
                    request,
                    Map.class
            );

            // 결제 성공
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data",    response.getBody()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}