package com.example.deliveats.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        @NotNull Long orderId,
        @Min(1) @Max(5) int rating,
        String content
) {
}
