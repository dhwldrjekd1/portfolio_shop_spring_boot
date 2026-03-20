package com.example.demo.item.item.controller;

import com.example.demo.item.item.dto.ItemRead;
import com.example.demo.item.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController //J.SON으로 출력할때 사용
@RequestMapping("/v1") //모든 http 매서드의 요청을 매핑하기 위한 애너테이션
@RequiredArgsConstructor //빈을 간편하게 생성자 주입할수 있게 해주는 애너테이션

public class ItemController {

    private final ItemService itemService; //서비스 선언

    @GetMapping("/api/items")
    public ResponseEntity<?> readAll() {
        List<ItemRead> items = itemService.findAll();
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
}
