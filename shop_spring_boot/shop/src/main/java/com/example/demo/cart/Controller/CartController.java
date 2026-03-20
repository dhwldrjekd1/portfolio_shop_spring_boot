package com.example.demo.cart.Controller;


import com.example.demo.account.helper.AccountHelper;
import com.example.demo.cart.dto.CartRead;
import com.example.demo.cart.dto.CartRequest;
import com.example.demo.cart.service.CartService;
import com.example.demo.item.item.dto.ItemRead;
import com.example.demo.item.item.service.ItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final ItemService itemService;
    private final AccountHelper accountHelper;

    @GetMapping("/api/cart/items")
    public ResponseEntity<?> readAll(HttpServletRequest req) {
        //로그인 회원 아이디
        Integer memberId = accountHelper.getMemberId(req);

        //장바구니 목록 조회
        List<CartRead> carts = cartService.findAll(memberId);

        //장바구니 안에 있는 상품 아이디로 상품을 조회
        List<Integer> itemIds = carts.stream().map(CartRead::getProductId).toList();
        List<ItemRead> items = itemService.findAll(itemIds);

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @PostMapping("/api/cart")
    public ResponseEntity<?> push(HttpServletRequest req, @RequestBody CartRequest cartReq) {

        //로그인 회원 아이디
        Integer memberId = accountHelper.getMemberId(req);

        //장바구니 데이터 조회(특정 상품)
        CartRead cart = cartService.find(memberId, cartReq.getProductId(), cartReq.getQuantity());

        //장바구니가 데이터가 없다면
        if(cart == null) {
            cartService.save(memberId, cartReq);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/api/cart/product/{productId}")
        public ResponseEntity<?> remove(HttpServletRequest req, @PathVariable("productId") Integer productId, @PathVariable("quantity") Integer quantity) {
        //로그인 회원 아이디
        Integer memberId = accountHelper.getMemberId(req);

        cartService.remove(memberId, productId, quantity);
        return new ResponseEntity<>(HttpStatus.OK);

    }
}
