package com.example.deliveats.domain.delivery;

import com.example.deliveats.domain.order.Order;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.DeliveryErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ------ 연관관계 ------- */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_id")
    private User rider;

    /* ------- 상태 -------- */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryStatus status;

    private Integer etaMinutes;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;

    /* ------- 생성자 -------- */
    protected Delivery(Order order) {
        this.order = order;
        this.status = DeliveryStatus.REQUESTED;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Order order;

        private Builder() {}

        public Builder order(Order order) {
            this.order = order;
            return this;
        }

        public Delivery build() {
            if (order == null) {
                throw new CustomException(DeliveryErrorCode.INVALID_DELIVERY,"order is required");
            }
            return new Delivery(order);
        }
    }

    /* ------ 도메인 로직 ------- */
    public void assignRider(User rider) {
        if (status != DeliveryStatus.REQUESTED) {
            throw new CustomException(DeliveryErrorCode.DELIVERY_INVALID_STATUS, "delivery must be REQUESTED to assign rider. current=" + status);
        }
        this.rider = rider;
        this.status = DeliveryStatus.ASSIGNED;
    }

    public void pickUp() {
        if (status != DeliveryStatus.ASSIGNED) {
            throw new CustomException(DeliveryErrorCode.DELIVERY_INVALID_STATUS, "delivery must be ASSIGNED to pick up. current=" + status);
        }
        this.status = DeliveryStatus.PICKED_UP;
        this.pickedUpAt = LocalDateTime.now();
    }

    public void startDelivering() {
        if (status != DeliveryStatus.PICKED_UP) {
            throw new CustomException(DeliveryErrorCode.DELIVERY_INVALID_STATUS, "delivery must be PICKED_UP to deliver. current=" + status);
        }
        this.status = DeliveryStatus.DELIVERING;
    }

    public void complete() {
        if (status != DeliveryStatus.DELIVERING) {
            throw new CustomException(DeliveryErrorCode.DELIVERY_INVALID_STATUS, "delivery must be DELIVERING to complete. current=" + status);
        }
        this.status = DeliveryStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    public void fail() {
        this.status = DeliveryStatus.FAILED;
    }
}
