package com.example.deliveats.domain.order;

import com.example.deliveats.domain.store.Store;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.OrderErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ------ 연관관계 ------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrderItem> items = new ArrayList<>();

    /* ------- 상태 -------- */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private int totalPrice;

    private LocalDateTime createdAt;

    /* ------- 생성자 -------- */
    protected Order(User user, Store store) {
        this.user = user;
        this.store = store;
        this.status = OrderStatus.PENDING;
        this.totalPrice = 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User user;
        private Store store;

        private Builder() {}

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder store(Store store) {
            this.store = store;
            return this;
        }

        public Order build() {
            if (user == null)
                throw new CustomException(OrderErrorCode.INVALID_ORDER, "customer(user) is required");
            if (store == null)
                throw new CustomException(OrderErrorCode.INVALID_ORDER, "store is required");

            return new Order(user, store);
        }
    }

    /* ------ 도메인 로직 ------- */
    public void addItem(OrderItem item) {
        if (item == null) {
            throw new CustomException(OrderErrorCode.INVALID_ORDER, "order item cannot be null");
        }
        items.add(item);
        totalPrice += item.getPriceAtOrder() * item.getQuantity();
    }

    public void markPaid() {
        if (status != OrderStatus.CREATED && status != OrderStatus.PENDING) {
            throw new CustomException(OrderErrorCode.INVALID_ORDER_STATUS, "order cannot be paid when status=" + status);
        }
        this.status = OrderStatus.PAID;
    }

    public void accept() {
        if (status != OrderStatus.PAID) {
            throw new CustomException(OrderErrorCode.INVALID_ORDER_STATUS, "order must be PAID to accept. current=" + status);
        }
        this.status = OrderStatus.ACCEPTED;
    }

    public void startCooking() {
        if (status != OrderStatus.ACCEPTED) {
            throw new CustomException(OrderErrorCode.INVALID_ORDER_STATUS, "order must be ACCEPTED to cook. current=" + status);
        }
        this.status = OrderStatus.COOKING;
    }

    public void startDelivery() {
        if (status != OrderStatus.COOKING && status != OrderStatus.ACCEPTED) {
            throw new CustomException(OrderErrorCode.INVALID_ORDER_STATUS, "order must be COOKING or ACCEPTED to start delivery. current=" + status);
        }
        this.status = OrderStatus.DELIVERING;
    }

    public void complete() {
        if (status != OrderStatus.COOKING) {
            throw new CustomException(OrderErrorCode.INVALID_ORDER_STATUS, "order must be COOKING to complete. current=" + status);
        }
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        if (status == OrderStatus.COMPLETED) {
            throw new CustomException(OrderErrorCode.ORDER_ALREADY_COMPLETED, "completed order cannot be canceled");
        }
        this.status = OrderStatus.CANCELED;
    }

    @PrePersist
    protected void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
