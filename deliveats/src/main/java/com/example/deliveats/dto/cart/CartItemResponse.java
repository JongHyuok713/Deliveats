package com.example.deliveats.dto.cart;

import com.example.deliveats.domain.cart.CartItem;

public record CartItemResponse(
        Long itemId,
        Long menuId,
        String menuName,
        int price,
        String imageUrl,
        int quantity
) {
    public static CartItemResponse from(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                item.getMenu().getId(),
                item.getMenu().getName(),
                item.getMenu().getPrice(),
                item.getMenu().getImageUrl(),
                item.getQuantity()
        );
    }
}
