package com.example.demo.item.service;

import com.example.demo.config.FileConfig;
import com.example.demo.item.entity.Item;
import com.example.demo.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BaseItemService implements ItemService {

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Set<String> ALLOWED_IMAGE_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

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

        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("허용되지 않는 이미지 형식입니다.");
        }

        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase()
                : "";
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("허용되지 않는 파일 확장자입니다.");
        }
        validateImageMagicBytes(image);

        File dir = new File(fileConfig.uploadDir);
        if (!dir.exists()) dir.mkdirs();
        String filename = UUID.randomUUID() + "." + extension;
        File dest = new File(dir, filename);
        image.transferTo(dest);
        return "/uploads/" + filename;
    }

    // 파일 내용의 매직바이트(시그니처)를 확인해 확장자/Content-Type 위장을 방지.
    // 둘 다 클라이언트가 요청에서 임의로 설정 가능한 값이라, 실제 파일 내용까지 확인해야
    // "진짜 이미지 파일"임을 신뢰할 수 있다.
    private void validateImageMagicBytes(MultipartFile image) throws IOException {
        byte[] header = new byte[12];
        int read;
        try (InputStream in = image.getInputStream()) {
            read = in.readNBytes(header, 0, header.length);
        }
        boolean isJpeg = read >= 3
                && (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF;
        boolean isPng = read >= 4
                && (header[0] & 0xFF) == 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47;
        boolean isGif = read >= 4
                && header[0] == 'G' && header[1] == 'I' && header[2] == 'F' && header[3] == '8';
        boolean isWebp = read >= 12
                && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P';
        if (!isJpeg && !isPng && !isGif && !isWebp) {
            throw new IllegalArgumentException("파일 내용이 이미지 형식과 일치하지 않습니다.");
        }
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