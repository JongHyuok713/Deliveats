package com.example.deliveats.dto.cart;

import com.example.deliveats.domain.cart.Cart;

import java.util.List;

public record CartResponse(
        Long cartId,
        Long storeId,
        String storeName,
        List<CartItemResponse> items,
        int totalPrice
) {
    public static CartResponse from(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(CartItemResponse::from)
                .toList();

        int total = cart.getItems().stream()
                .mapToInt(i -> i.getMenu().getPrice() * i.getQuantity())
                .sum();

        return new CartResponse(
                cart.getId(),
                cart.getStore().getId(),
                cart.getStore().getName(),
                itemResponses,
                total
        );
    }
}
