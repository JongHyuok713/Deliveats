package com.example.deliveats.domain.cart;

import com.example.deliveats.domain.store.Menu;
import com.example.deliveats.exception.CartErrorCode;
import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.MenuErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private int quantity;

    protected CartItem(Cart cart, Menu menu, int quantity) {
        this.cart = cart;
        this.menu = menu;
        this.quantity = quantity;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Cart cart;
        private Menu menu;
        private int quantity = 1;

        private Builder() {}

        public Builder cart(Cart cart) {
            this.cart = cart;
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

        public CartItem build() {
            if (cart == null) {
                throw new CustomException(CartErrorCode.INVALID_CART, "cart is required");
            }
            if (menu == null) {
                throw new CustomException(MenuErrorCode.MENU_NOT_FOUND, "menu is required");
            }
            if (quantity <= 0) {
                throw new CustomException(CartErrorCode.INVALID_CART, "quantity must be positive");
            }

            CartItem item = new CartItem(cart, menu, quantity);
            cart.addItem(item);
            return item;
        }
    }

    /* ------ 도메인 로직 ------- */

    public void increase(int amount) {
        if (amount <= 0) {
            throw new CustomException(CartErrorCode.INVALID_CART, "amount must be positive");
        }
        this.quantity += amount;
    }

    public void updateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new CustomException(CartErrorCode.INVALID_CART, "quantity must be positive");
        }
        this.quantity = quantity;
    }

    public int getTotalPrice() {
        return menu.getPrice() * quantity;
    }
}

