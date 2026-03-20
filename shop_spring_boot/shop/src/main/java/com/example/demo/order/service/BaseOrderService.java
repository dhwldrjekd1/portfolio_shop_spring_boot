package com.example.demo.order.service;

import com.example.demo.cart.service.CartService;
import com.example.demo.item.item.dto.ItemRead;
import com.example.demo.item.item.service.ItemService;
import com.example.demo.order.dto.OrderRead;
import com.example.demo.order.dto.OrderRequest;
import com.example.demo.order.entity.OrderEntity;
import com.example.demo.order.entity.OrderItemEntity;
import com.example.demo.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BaseOrderService implements OrderService {

    private final OrderRepository orderRepository; // ✅ orderItemRepository -> orderRepository 수정
    private final OrderItemService orderItemService;
    private final ItemService itemService;
    private final CartService cartService;

    // 주문 목록 조회
    @Override
    public List<OrderRead> findAll(Integer memberId) {
        return orderRepository.findAllByMemberIdOrderByIdDesc(memberId).stream() // ✅ orderItemRepository -> orderRepository 수정
                .map(OrderEntity::toRead) // ✅ (Order::toRead) 괄호 오류 수정
                .toList();
    }

    // 주문 상세 조회
    @Override
    public OrderRead find(Integer id, Integer memberId) {
        Optional<OrderEntity> orderOptional = orderRepository.findByIdAndMemberId(id, memberId); // ✅ Order -> OrderEntity, orderItemRepository -> orderRepository 수정

        if (orderOptional.isPresent()) {
            OrderRead order = orderOptional.get().toRead();

            List<OrderItemEntity> orderItems = orderItemService.findAll(order.getId()); // ✅ OrderItem -> OrderItemEntity 수정

            List<Integer> orderItemIds = orderItems.stream().map(OrderItemEntity::getItemId).toList(); // ✅ orderItem::getItemId -> OrderItemEntity::getItemId 수정

            List<ItemRead> items = itemService.findAll(orderItemIds);

            order.setItems(items);

            return order;
        }
        return null;
    }

    // 주문 저장
    @Override
    @Transactional
    public void order(OrderRequest orderReq, Integer memberId) {
        List<ItemRead> items = itemService.findAll(orderReq.getItemIds()); // ✅ orderReq.getItemIds()로 수정 (orderReq, getItemIds() 오류)

        long amount = 0L;
        for (ItemRead item : items) {
            amount += item.getPrice() - item.getPrice().longValue() * item.getDiscountPer() / 100;
        }

        orderReq.setAmount(amount);

        OrderEntity order = orderRepository.save(orderReq.toEntity(memberId)); // ✅ Order -> OrderEntity, 대문자 OrderRepository -> orderRepository 수정

        List<OrderItemEntity> newOrderItems = new ArrayList<>(); // ✅ OrderItem -> OrderItemEntity 수정

        orderReq.getItemIds().forEach(itemId -> {
            OrderItemEntity newOrderItem = new OrderItemEntity(order.getId(), itemId, 1); // ✅ OrderItem -> OrderItemEntity 수정
            newOrderItems.add(newOrderItem);
        }); // ✅ 괄호 오류 수정

        orderItemService.saveAll(newOrderItems);

        cartService.removeAll(order.getMemberId());
    }
}