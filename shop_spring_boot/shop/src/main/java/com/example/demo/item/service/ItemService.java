package com.example.demo.item.service;

import com.example.demo.item.entity.Item;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ItemService {
    List<Item> findAll();
    Item findById(Integer id);

    // 상품 등록 (이미지 파일 또는 URL, 카테고리, 뱃지 포함)
    Item save(String name, Double price, Double discountRate, Integer stock,
              String description, MultipartFile image, String imageUrl,
              String category, String badge);

    // 상품 수정 (이미지 파일 또는 URL, 카테고리, 뱃지 포함)
    void update(Integer id, String name, Double price, Double discountRate, Integer stock,
                String description, MultipartFile image, String imageUrl,
                String category, String badge);

    // 상품 세부정보 수정 (JSON 문자열)
    void updateDetails(Integer id, String detailsJson);

    // 상품 삭제
    void delete(Integer id);

    // 재고 수정
    void updateStock(Integer id, Integer stock);

    // 할인율 수정
    void updateDiscountRate(Integer id, Double discountRate);
}