package com.example.demo.item.item.service;

import com.example.demo.item.entity.ItemEntity;
import com.example.demo.item.item.dto.ItemRead;
import com.example.demo.item.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service //서비스 애너테이션임을 선언
@RequiredArgsConstructor //빈을 간편하게 생성자주입 할수 있게 해주는 애너테이션
public class BaseItemService implements ItemService {
    private final ItemRepository itemRepository;

    //전체 상품 목록 조회
    @Override
    public List<ItemRead> findAll() {
        return itemRepository.findAll().stream().map(ItemEntity::toRead).toList();
        //itemRepository: 스프링 컨테이너에 의해 주입될 상품 레포지터리 필드
    }

    //상품 목록 조회(특정 아이디 리스트로 조회)
    @Override
    public List<ItemRead> findAll(List<Integer> ids) {
        return itemRepository.findAllByIdIn(ids).stream().map(ItemEntity::toRead).toList();
    }
}
