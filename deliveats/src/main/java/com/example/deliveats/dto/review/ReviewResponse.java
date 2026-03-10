package com.example.deliveats.dto.review;

import com.example.deliveats.domain.review.Review;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReviewResponse(Long id,
                             String username,
                             String storeName,
                             int rating,
                             String content,
                             LocalDateTime createAt
) {
    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .username(review.getUser().getUsername())
                .storeName(review.getStore().getName())
                .rating(review.getRating())
                .content(review.getContent())
                .createAt(review.getCreatedAt())
                .build();
    }
}
