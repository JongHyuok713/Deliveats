package com.example.deliveats.dto.store;

import com.example.deliveats.domain.store.Store;
import lombok.Builder;

import java.util.List;

@Builder
public record StoreResponse(
        Long id,
        String name,
        String category,
        String address,
        Integer minOrderPrice,
        String mainImage,
        Long ownerId,
        List<MenuResponse> menus
) {

    public static StoreResponse from(Store store, List<MenuResponse> menus) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .category(store.getCategory())
                .address(store.getAddress())
                .minOrderPrice(store.getMinOrderPrice())
                .mainImage(store.getMainImage())
                .ownerId(store.getOwner().getId())
                .menus(menus)
                .build();
    }
}
