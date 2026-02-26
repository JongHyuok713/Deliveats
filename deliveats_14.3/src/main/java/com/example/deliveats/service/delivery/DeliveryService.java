package com.example.deliveats.service.delivery;

import com.example.deliveats.domain.delivery.Delivery;
import com.example.deliveats.domain.delivery.DeliveryStatus;
import com.example.deliveats.domain.order.Order;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.DeliveryErrorCode;
import com.example.deliveats.exception.delivery.DeliveryNotFoundException;
import com.example.deliveats.exception.order.OrderNotFoundException;
import com.example.deliveats.repository.delivery.DeliveryRepository;
import com.example.deliveats.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;

    @Transactional // 라이더에게 주문 배정
    public Delivery assignRider(Long orderId, User rider) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseGet(() -> Delivery.builder()
                        .order(order)
                        .build());

        delivery.assignRider(rider);
        order.startDelivery();

        return deliveryRepository.save(delivery);
    }

    @Transactional // 배달 상태 변경
    public void updateStatus(Long deliveryId, DeliveryStatus status) {
        if (status == null) {
            throw new CustomException(DeliveryErrorCode.DELIVERY_INVALID_STATUS, "delivery status is required");
        }

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(DeliveryNotFoundException::new);

        switch (status) {
            case PICKED_UP -> delivery.pickUp();
            case DELIVERING -> delivery.startDelivering();
            case DELIVERED -> delivery.complete();
            case FAILED -> delivery.fail();
            default -> throw new CustomException(
                    DeliveryErrorCode.DELIVERY_INVALID_STATUS,
                    "unsupported delivery status: " + status
            );
        }
    }

    // 라이더 본인의 배달 목록
    public List<Delivery> getMyDeliveries(User rider) {
        return deliveryRepository.findByRider(rider);
    }

    // 상태별 배달 목록 (관리자용)
    public List<Delivery> getDeliveriesByStatus(DeliveryStatus status) {
        if (status == null) {
            return deliveryRepository.findAll();
        }
        return deliveryRepository.findByStatus(status);
    }
}
