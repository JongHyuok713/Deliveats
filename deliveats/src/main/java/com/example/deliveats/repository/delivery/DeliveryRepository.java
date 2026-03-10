package com.example.deliveats.repository.delivery;

import com.example.deliveats.domain.delivery.Delivery;
import com.example.deliveats.domain.delivery.DeliveryStatus;
import com.example.deliveats.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByOrderId(Long orderId);
    List<Delivery> findByRider(User rider);
    List<Delivery> findByStatus(DeliveryStatus status);
}
