package com.example.deliveats.dto.store;

public record StoreUpdateRequest(
        String name,
        String category,
        String address,
        Integer minOrderPrice,
        String mainImage
) {
}
