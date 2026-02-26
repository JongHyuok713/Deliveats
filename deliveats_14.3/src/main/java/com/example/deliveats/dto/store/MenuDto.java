package com.example.deliveats.dto.store;

import com.example.deliveats.domain.store.Menu;
import lombok.Builder;

@Builder
public record MenuDto(Long id,
                      String name,
                      int price,
                      String imageUrl,
                      Boolean isAvailable
) {
    public static MenuDto from(Menu menu) {
        return MenuDto.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .imageUrl(menu.getImageUrl())
                .isAvailable(menu.isAvailable())
                .build();
    }
}
