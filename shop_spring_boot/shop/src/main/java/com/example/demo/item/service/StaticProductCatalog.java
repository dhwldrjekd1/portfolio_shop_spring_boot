package com.example.demo.item.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// DB items 테이블에는 없지만 프론트엔드 정적 카탈로그(products.json)에만 존재하는
// 초기 시드 상품의 가격 조회용. products.json은 서버가 직접 배포하는 정적 리소스라 신뢰 가능.
@Component
public class StaticProductCatalog {

    public record Price(double price, double discountRate) {}

    private final Map<Integer, Price> catalog = new HashMap<>();

    public StaticProductCatalog() {
        try (InputStream is = new ClassPathResource("static/web03/products.json").getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);
            for (JsonNode p : root.path("products")) {
                int id = p.path("id").asInt();
                double price = p.path("price").asDouble();
                double discountRate = p.path("discountRate").asDouble(0);
                catalog.put(id, new Price(price, discountRate));
            }
        } catch (IOException e) {
            // 정적 카탈로그 로드 실패 시 빈 상태로 유지 (DB 상품만으로 검증)
        }
    }

    public Optional<Price> find(Integer id) {
        return Optional.ofNullable(catalog.get(id));
    }
}
