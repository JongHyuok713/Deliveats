package com.example.deliveats.service.review;

import com.example.deliveats.domain.order.Order;
import com.example.deliveats.domain.review.Review;
import com.example.deliveats.domain.store.Store;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.exception.CommonErrorCode;
import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.ReviewErrorCode;
import com.example.deliveats.exception.order.OrderNotFoundException;
import com.example.deliveats.exception.store.StoreNotFoundException;
import com.example.deliveats.repository.order.OrderRepository;
import com.example.deliveats.repository.review.ReviewRepository;
import com.example.deliveats.repository.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public Review createReview(User user, Long orderId, int rating, String content) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if (!order.getUser().getId().equals(user.getId())) {
            throw new CustomException(CommonErrorCode.FORBIDDEN);
        }
        if (reviewRepository.existsByUserAndOrderId(user, orderId)) {
            throw new CustomException(ReviewErrorCode.REVIEW_ALREADY_EXISTS, "review already exists for this order");
        }

        Review review = Review.builder()
                .user(user)
                .store(order.getStore())
                .order(order)
                .rating(rating)
                .content(content)
                .build();
        return reviewRepository.save(review);
    }

    public List<Review> getStoreReviews(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);

        return reviewRepository.findByStore(store);
    }
}
