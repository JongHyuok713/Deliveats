package com.example.deliveats.repository.cart;

import com.example.deliveats.domain.cart.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"store", "items", "items.menu"})
    Optional<Cart> findWithItemsByUserId(Long userId);
}
