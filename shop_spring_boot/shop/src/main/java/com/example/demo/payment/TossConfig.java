package com.example.demo.config.toss;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class TossConfig {

    // application.yml에서 주입
    @Value("${toss.client-key}")
    private String clientKey;

    @Value("${toss.secret-key}")
    private String secretKey;

    @Value("${toss.security-key}")
    private String securityKey;

    // 토스페이 결제 승인 URL
    public static final String CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
}