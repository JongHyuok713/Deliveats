package com.example.deliveats.repository.store;

import com.example.deliveats.domain.store.Store;
import com.example.deliveats.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("""
        SELECT s FROM Store s
        WHERE (:category IS NULL OR s.category = :category)
        AND (:keyword IS NULL OR s.name LIKE %:keyword%)
    """)
    List<Store> searchStores(String category, String keyword);

    List<Store> findByOwner(User owner);

    Optional<Store> findByOwnerId(Long ownerId);
    boolean existsByOwner(User owner);
}
