package com.example.deliveats.repository.order;

import com.example.deliveats.domain.order.Order;
import com.example.deliveats.domain.order.OrderStatus;
import com.example.deliveats.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserAndStatus(User user, OrderStatus status, Pageable pageable);
    Page<Order> findByUser(User user, Pageable pageable);
}
