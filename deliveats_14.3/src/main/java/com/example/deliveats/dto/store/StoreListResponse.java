package com.example.deliveats.dto.store;

public record StoreListResponse(
        Long id,
        String name,
        String category,
        String address,
        int minOrderPrice,
        String imageUrl
) {
}
