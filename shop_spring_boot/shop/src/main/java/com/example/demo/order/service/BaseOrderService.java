package com.example.demo.order.service;

import com.example.demo.cart.service.CartService;
import com.example.demo.item.entity.Item;
import com.example.demo.item.repository.ItemRepository;
import com.example.demo.item.service.StaticProductCatalog;
import com.example.demo.member.service.MemberService;
import com.example.demo.order.entity.Order;
import com.example.demo.order.entity.OrderItem;
import com.example.demo.order.repository.OrderRepository;
import com.example.demo.payment.entity.TossPayment;
import com.example.demo.payment.repository.TossPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BaseOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ItemRepository itemRepository;
    private final StaticProductCatalog staticProductCatalog;
    private final MemberService memberService; // 등급 업데이트용
    private final TossPaymentRepository tossPaymentRepository;

    // 허용된 결제 수단 목록 - 대소문자/공백 변형으로 "toss" 검증을 우회하는 것을 막기 위해
    // 결제 수단 문자열을 정규화(trim + lowercase)한 뒤 이 목록과 비교한다.
    private static final Set<String> ALLOWED_PAYMENTS = Set.of("toss", "kakao", "naver", "bank");

    @Override
    @Transactional
    public Order save(String loginId, String name, String address, String payment, String cardNumber, Integer amount, List<Map<String, Object>> items, String tossOrderId) {
        String normalizedPayment = payment == null ? null : payment.trim().toLowerCase();
        if (normalizedPayment == null || !ALLOWED_PAYMENTS.contains(normalizedPayment)) {
            throw new IllegalArgumentException("지원하지 않는 결제 수단입니다.");
        }
        validateAmount(amount, items);
        if ("toss".equals(normalizedPayment)) {
            verifyTossPayment(tossOrderId, amount);
        }
        decreaseStockForOrder(items);

        Order order = new Order(loginId, name, address, normalizedPayment, cardNumber, amount);
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
        // 프론트(store.getDiscountedPrice)와 동일하게, 할인이 없는 상품은 원가를 반올림하지 않고 그대로 합산한다.
        // (할인 상품만 100원 단위로 반올림 - 프론트와 정확히 같은 계산이어야 금액 검증 오차가 생기지 않는다)
        double subtotal = 0;
        for (Map<String, Object> item : items) {
            Integer itemId = (Integer) item.get("itemId");
            Integer quantity = (Integer) item.get("quantity");
            if (quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("주문 수량이 올바르지 않습니다.");
            }

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

    // 토스 결제 주문(payment="toss")은 실제 승인된 결제와 연결되어야만 주문을 생성할 수 있도록 검증.
    // - 결제 없이 /api/order를 직접 호출해 주문을 만드는 것을 막고,
    // - 동일한 결제(paymentKey)로 여러 주문이 생성되는 것(재사용)을 막는다.
    private void verifyTossPayment(String tossOrderId, Integer amount) {
        if (tossOrderId == null || tossOrderId.isBlank()) {
            throw new IllegalStateException("결제 승인 정보가 없습니다.");
        }
        TossPayment payment = tossPaymentRepository.findByTossOrderId(tossOrderId)
                .orElseThrow(() -> new IllegalStateException("결제 승인 내역을 찾을 수 없습니다."));
        if (!payment.getAmount().equals(amount)) {
            throw new IllegalStateException("결제 금액이 주문 금액과 일치하지 않습니다.");
        }
        // used=false → true 전환을 원자적 UPDATE로 처리 - 동시에 들어온 두 요청이 모두
        // "아직 사용 안 됨"을 확인한 뒤 둘 다 통과하는 레이스를 막는다 (재고 차감과 동일한 패턴).
        int updated = tossPaymentRepository.markUsedIfUnused(tossOrderId);
        if (updated == 0) {
            throw new IllegalStateException("이미 사용된 결제입니다.");
        }
    }

    // 주문 생성 시점에 재고를 원자적으로 차감. 동시 주문으로 재고가 이미 소진된 경우 예외 발생 → 트랜잭션 롤백.
    // 정적 시드 카탈로그 상품(DB에 없는 상품)은 재고 추적 대상이 아니므로 건너뛴다.
    private void decreaseStockForOrder(List<Map<String, Object>> items) {
        for (Map<String, Object> item : items) {
            Integer itemId = (Integer) item.get("itemId");
            Integer quantity = (Integer) item.get("quantity");
            if (!itemRepository.existsById(itemId)) continue;

            int updated = itemRepository.decreaseStockIfAvailable(itemId, quantity);
            if (updated == 0) {
                String itemName = (String) item.get("itemName");
                throw new IllegalStateException("재고가 부족합니다: " + (itemName != null ? itemName : itemId));
            }
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

    // 주문 삭제 (관리자) - 취소되지 않은 주문이면 삭제 전 재고를 복구한다 (재고는 주문 생성 시점에 차감되므로).
    @Override
    @Transactional
    public void delete(Integer id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null && !"취소".equals(order.getStatus())) {
            for (OrderItem item : order.getOrderItems()) {
                if (itemRepository.existsById(item.getItemId())) {
                    itemRepository.increaseStock(item.getItemId(), item.getQuantity());
                }
            }
        }
        orderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Integer id, String status) {
        // 행 잠금 조회 - 동시에 들어온 취소 요청이 재고를 이중으로 복구하지 않도록 함
        Order order = orderRepository.findByIdForUpdate(id).orElseThrow();
        String prevStatus = order.getStatus();
        order.updateStatus(status);
        orderRepository.save(order);

        // 취소 시 재고 복구 (재고는 주문 생성 시점에 이미 차감했으므로, 취소되지 않았던 주문이 취소될 때 항상 복구)
        if ("취소".equals(status) && !"취소".equals(prevStatus)) {
            for (OrderItem item : order.getOrderItems()) {
                if (itemRepository.existsById(item.getItemId())) {
                    itemRepository.increaseStock(item.getItemId(), item.getQuantity());
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