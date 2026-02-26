package com.example.deliveats.dto.order;

import com.example.deliveats.domain.order.Order;
import com.example.deliveats.domain.order.OrderStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderListResponse(
        Long id,
        String storeName,
        OrderStatus status,
        int totalPrice,
        LocalDateTime createAt
) {
    public static OrderListResponse from(Order order) {
        return new OrderListResponse(
                order.getId(),
                order.getStore().getName(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt()
        );
    }
}
