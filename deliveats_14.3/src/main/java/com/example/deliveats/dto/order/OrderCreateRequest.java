package com.example.deliveats.dto.order;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderCreateRequest(
        @NotNull Long storeId,
        @NotNull List<Item> items
) {
    public record Item(@NotNull Long menuId, int quantity) {}
}
