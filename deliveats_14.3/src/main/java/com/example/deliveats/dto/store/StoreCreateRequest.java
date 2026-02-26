package com.example.deliveats.dto.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoreCreateRequest(
        @NotBlank String name,
        @NotBlank String category,
        @NotBlank String address,
        @NotNull Integer minOrderPrice
) {
    public record MenuCreateRequest(
            @NotBlank String name,
            @NotNull Integer price,
            String imageUrl
    ) {}
}
