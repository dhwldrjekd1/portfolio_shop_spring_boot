package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//시간 자동 처리
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}