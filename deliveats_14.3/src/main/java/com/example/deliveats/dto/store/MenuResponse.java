package com.example.deliveats.dto.store;

import com.example.deliveats.domain.store.Menu;
import lombok.Builder;

@Builder
public record MenuResponse(
        Long id,
        String name,
        int price,
        String imageUrl,
        boolean isAvailable
) {

    public static MenuResponse from(Menu m) {
        return MenuResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .price(m.getPrice())
                .isAvailable(m.isAvailable())
                .build();
    }
}
