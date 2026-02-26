package com.example.deliveats.dto.store;

import com.example.deliveats.dto.review.ReviewResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record MyStoreResponse(
        StoreResponse store,
        List<MenuResponse> menus,
        List<ReviewResponse> reviews
) {
}
