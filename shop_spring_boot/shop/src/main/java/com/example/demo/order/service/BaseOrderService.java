package com.example.demo.order.service;

import com.example.demo.cart.service.CartService;
import com.example.demo.item.entity.Item;
import com.example.demo.item.repository.ItemRepository;
import com.example.demo.item.service.StaticProductCatalog;
import com.example.demo.member.service.MemberService;
import com.example.demo.order.entity.Order;
import com.example.demo.order.entity.OrderItem;
import com.example.demo.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BaseOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ItemRepository itemRepository;
    private final StaticProductCatalog staticProductCatalog;
    private final MemberService memberService; // 등급 업데이트용

    @Override
    public Order save(String loginId, String name, String address, String payment, String cardNumber, Integer amount, List<Map<String, Object>> items) {
        validateAmount(amount, items);

        Order order = new Order(loginId, name, address, payment, cardNumber, amount);
        orderRepository.save(order);

        // 주문 상품 저장
        for (Map<String, Object> item : items) {
            Integer itemId = (Integer) item.get("itemId");
            Integer quantity = (Integer) item.get("quantity");
            String itemName = (String) item.get("itemName");
            String color = (String) item.get("color");
            String size = (String) item.get("size");
            OrderItem orderItem = new OrderItem(itemId, quantity, itemName, color, size, order);
            order.getOrderItems().add(orderItem);
        }
        orderRepository.save(order);

        // 장바구니 비우기
        cartService.deleteAll(loginId);

        // 총 구매금액 계산 후 자동 등급 업데이트
        List<Order> allOrders = orderRepository.findByLoginIdOrderByCreatedDesc(loginId);
        int totalAmount = allOrders.stream()
                .filter(o -> !"취소".equals(o.getStatus()))
                .mapToInt(Order::getAmount)
                .sum();
        memberService.updateGradeByAmount(loginId, totalAmount);

        return order;
    }

    // 결제수단과 무관하게, 클라이언트가 보낸 금액이 실제 상품가(할인 반영) 합계 + 배송비와 일치하는지 검증.
    // 상품은 DB(items)에 없으면 정적 시드 카탈로그(products.json)에서 조회한다.
    private void validateAmount(Integer amount, List<Map<String, Object>> items) {
        double subtotal = 0;
        for (Map<String, Object> item : items) {
            Integer itemId = (Integer) item.get("itemId");
            Integer quantity = (Integer) item.get("quantity");

            double price;
            double discountRate;
            Item dbItem = itemRepository.findById(itemId).orElse(null);
            if (dbItem != null) {
                price = dbItem.getPrice();
                discountRate = dbItem.getDiscountRate() != null ? dbItem.getDiscountRate() : 0;
            } else {
                StaticProductCatalog.Price staticPrice = staticProductCatalog.find(itemId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품이 포함되어 있습니다."));
                price = staticPrice.price();
                discountRate = staticPrice.discountRate();
            }

            double unitPrice = discountRate > 0
                    ? Math.round((price * (1 - discountRate / 100)) / 100.0) * 100
                    : price;
            subtotal += unitPrice * quantity;
        }

        double shipping = subtotal >= 50000 ? 0 : 3000;
        double expected = subtotal + shipping;

        if (amount == null || Math.abs(expected - amount) > 1) {
            throw new IllegalArgumentException("주문 금액이 올바르지 않습니다.");
        }
    }

    @Override
    public List<Order> findAll(String loginId) {
        return orderRepository.findByLoginIdOrderByCreatedDesc(loginId);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAllByOrderByCreatedDesc();
    }

    @Override
    public Order findById(Integer id) {
        return orderRepository.findById(id).orElseThrow();
    }

    // 주문 삭제 (관리자)
    @Override
    public void delete(Integer id) {
        orderRepository.deleteById(id);
    }

    @Override
    public void updateStatus(Integer id, String status) {
        Order order = orderRepository.findById(id).orElseThrow();
        String prevStatus = order.getStatus();
        order.updateStatus(status);
        orderRepository.save(order);

        // 배송완료 시 재고 차감
        if ("배송완료".equals(status) && !"배송완료".equals(prevStatus)) {
            for (OrderItem item : order.getOrderItems()) {
                Item dbItem = itemRepository.findById(item.getItemId()).orElse(null);
                if (dbItem != null) {
                    int newStock = Math.max(0, dbItem.getStock() - item.getQuantity());
                    dbItem.updateStock(newStock);
                    itemRepository.save(dbItem);
                }
            }
        }

        // 취소 시 재고 복구 (배송완료였던 경우만)
        if ("취소".equals(status) && "배송완료".equals(prevStatus)) {
            for (OrderItem item : order.getOrderItems()) {
                Item dbItem = itemRepository.findById(item.getItemId()).orElse(null);
                if (dbItem != null) {
                    dbItem.updateStock(dbItem.getStock() + item.getQuantity());
                    itemRepository.save(dbItem);
                }
            }
        }

        // 취소 시 등급 재계산
        if ("취소".equals(status)) {
            List<Order> allOrders = orderRepository.findByLoginIdOrderByCreatedDesc(order.getLoginId());
            int totalAmount = allOrders.stream()
                    .filter(o -> !"취소".equals(o.getStatus()))
                    .mapToInt(Order::getAmount)
                    .sum();
            memberService.updateGradeByAmount(order.getLoginId(), totalAmount);
        }
    }
}