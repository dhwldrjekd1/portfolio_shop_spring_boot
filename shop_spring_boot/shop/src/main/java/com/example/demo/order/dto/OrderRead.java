package com.example.demo.order.dto;

import com.example.demo.item.item.dto.ItemRead;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderRead {

    private Integer id;
    private String name;
    private String address;
    private String payment;
    private Long amount;
    private LocalDateTime createdAt;
    private List<ItemRead> items;
}
