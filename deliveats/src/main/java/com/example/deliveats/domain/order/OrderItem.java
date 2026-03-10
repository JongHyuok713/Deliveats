package com.example.deliveats.domain.order;

import com.example.deliveats.domain.store.Menu;
import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.MenuErrorCode;
import com.example.deliveats.exception.OrderErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ------ 연관관계 ------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    /* 주문 정보 */
    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer priceAtOrder;

    /* ------- 생성자 -------- */
    protected OrderItem(
            Order order,
            Menu menu,
            int quantity,
            int priceAtOrder
    ) {
        this.order = order;
        this.menu = menu;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Order order;
        private Menu menu;
        private Integer quantity;
        private Integer priceAtOrder;

        private Builder() {}

        public Builder order(Order order) {
            this.order = order;
            return this;
        }

        public Builder menu(Menu menu) {
            this.menu = menu;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder priceAtOrder(int priceAtOrder) {
            this.priceAtOrder = priceAtOrder;
            return this;
        }

        public OrderItem build() {
            if (order == null) {
                throw new CustomException(OrderErrorCode.INVALID_ORDER, "order is required");
            }
            if (menu == null) {
                throw new CustomException(MenuErrorCode.MENU_NOT_FOUND, "menu is required");
            }
            if (quantity == null || quantity <= 0) {
                throw new CustomException(OrderErrorCode.INVALID_ORDER, "quantity must be greater than 0");
            }
            if (priceAtOrder == null || priceAtOrder < 0) {
                throw new CustomException(OrderErrorCode.INVALID_ORDER, "priceAtOrder must be >= 0");
            }

            return new OrderItem(order, menu, quantity, priceAtOrder);
        }
    }

    /* ------ 도메인 로직 ------- */
    public int calculateTotalPrice() {
        return priceAtOrder * quantity;
    }

    public void changeQuantity(int quantity) {
        if (quantity <= 0) {
            throw new CustomException(OrderErrorCode.INVALID_ORDER, "quantity must be greater than 0");
        }
        this.quantity = quantity;
    }

}
