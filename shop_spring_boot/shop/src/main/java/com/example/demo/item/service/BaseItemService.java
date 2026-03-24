package com.example.demo.item.service;

import com.example.demo.config.FileConfig;
import com.example.demo.item.entity.Item;
import com.example.demo.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BaseItemService implements ItemService {

    private final ItemRepository itemRepository;
    private final FileConfig fileConfig;

    // 전체 상품 조회
    @Override
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    // 단일 상품 조회
    @Override
    public Item findById(Integer id) {
        return itemRepository.findById(id).orElseThrow();
    }

    // 이미지 저장 (파일)
    private String saveImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) return null;
        File dir = new File(fileConfig.uploadDir);
        if (!dir.exists()) dir.mkdirs();
        String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
        File dest = new File(dir, filename);
        image.transferTo(dest);
        return "/uploads/" + filename;
    }

    // 상품 등록
    @Override
    public Item save(String name, Double price, Double discountRate, Integer stock,
                     String description, MultipartFile image, String imageUrl,
                     String category, String badge) {
        try {
            String imagePath = saveImage(image);
            if (imagePath == null && imageUrl != null && !imageUrl.isEmpty()) {
                imagePath = imageUrl;
            }
            Item item = new Item(name, imagePath, price, discountRate, stock, description, category, badge);
            return itemRepository.save(item);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패: " + e.getMessage());
        }
    }

    // 상품 수정
    @Override
    public void update(Integer id, String name, Double price, Double discountRate, Integer stock,
                       String description, MultipartFile image, String imageUrl,
                       String category, String badge) {
        try {
            Item item = itemRepository.findById(id).orElseThrow();
            String imagePath = saveImage(image);
            if (imagePath == null && imageUrl != null && !imageUrl.isEmpty()) {
                imagePath = imageUrl;
            }
            item.update(name, imagePath, price, discountRate, stock, description, category, badge);
            itemRepository.save(item);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패: " + e.getMessage());
        }
    }

    // 상품 삭제
    @Override
    public void delete(Integer id) {
        itemRepository.deleteById(id);
    }

    // 재고 수정
    @Override
    public void updateStock(Integer id, Integer stock) {
        Item item = itemRepository.findById(id).orElseThrow();
        item.updateStock(stock);
        itemRepository.save(item);
    }

    // 할인율 수정
    @Override
    public void updateDiscountRate(Integer id, Double discountRate) {
        Item item = itemRepository.findById(id).orElseThrow();
        item.updateDiscountRate(discountRate);
        itemRepository.save(item);
    }

    // 세부정보 수정 (JSON 문자열로 저장)
    @Override
    public void updateDetails(Integer id, String detailsJson) {
        Item item = itemRepository.findById(id).orElseThrow();
        item.updateDetails(detailsJson);
        itemRepository.save(item);
    }
}