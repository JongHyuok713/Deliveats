package com.example.deliveats.controller.review;

import com.example.deliveats.domain.review.Review;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.security.CustomUserDetails;
import com.example.deliveats.service.review.ReviewService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 요청 DTO
    private record ReviewRequest(
            @NotNull Long orderId,
            @Min(1) @Max(5) int rating,
            String content
    ) {}

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Review> createReview(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.createReview(userDetails.getUser(), request.orderId(), request.rating(), request.content()));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Review>> getStoreReviews(@PathVariable Long storeId) {
        return ResponseEntity.ok(reviewService.getStoreReviews(storeId));
    }
}
