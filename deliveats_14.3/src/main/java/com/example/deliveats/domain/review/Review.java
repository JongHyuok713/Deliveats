package com.example.deliveats.domain.review;

import com.example.deliveats.domain.order.Order;
import com.example.deliveats.domain.store.Store;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.exception.CommonErrorCode;
import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.ReviewErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;

    protected Review(
            User user,
            Store store,
            Order order,
            Integer rating,
            String content
    ) {
        this.user = user;
        this.store = store;
        this.order = order;
        this.rating = rating;
        this.content = content;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User user;
        private Store store;
        private Order order;
        private Integer rating;
        private String content;

        private Builder() {}

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder store(Store store) {
            this.store = store;
            return this;
        }

        public Builder order(Order order) {
            this.order = order;
            return this;
        }

        public Builder rating(Integer rating) {
            this.rating = rating;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Review build() {
            if (user == null || store == null || order == null) {
                throw new CustomException(CommonErrorCode.UNAUTHORIZED,"user, store, order are required");
            }
            if (rating == null || rating < 1 || rating > 5) {
                throw new CustomException(ReviewErrorCode.REVIEW_RATING_ERROR,"rating must be between 1 and 5");
            }
            return new Review(user, store, order, rating, content);
        }
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
