package com.example.deliveats.dto.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenuUpdateRequest(
        @NotBlank String name,
        @NotNull Integer price,
        String imageUrl,
        @NotNull Boolean isAvailable
) {}
