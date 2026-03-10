package com.example.deliveats.repository.review;

import com.example.deliveats.domain.review.Review;
import com.example.deliveats.domain.store.Store;
import com.example.deliveats.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByStore(Store store);
    Optional<Review> findByUserAndOrderId(User user, Long orderId);
    boolean existsByUserAndOrderId(User user, Long orderId);
}
