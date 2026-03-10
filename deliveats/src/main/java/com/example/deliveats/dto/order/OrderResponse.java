package com.example.deliveats.dto.order;

import com.example.deliveats.domain.order.Order;
import com.example.deliveats.domain.order.OrderItem;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderResponse(
        Long id,
        Long storeId,
        String storeName,
        int totalPrice,
        String status,
        LocalDateTime createdAt,
        List<Item> items
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStore().getId(),
                order.getStore().getName(),
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getItems().stream().map(Item::from).toList()
        );
    }

    public record Item(
            Long menuId,
            String menuName,
            int priceAtOrder,
            int quantity
    ) {
        public static Item from(OrderItem item) {
            return new Item(
                    item.getMenu().getId(),
                    item.getMenu().getName(),
                    item.getPriceAtOrder(),
                    item.getQuantity());
        }
    }
}
