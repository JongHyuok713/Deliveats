package com.example.deliveats.domain.cart;

import com.example.deliveats.domain.store.Store;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.exception.CartErrorCode;
import com.example.deliveats.exception.CustomException;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;

    protected Cart(User user, Store store) {
        this.user = user;
        this.store = store;
        this.items = new ArrayList<>();
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

        public Cart build() {
            if (user == null) {
                throw new CustomException(CartErrorCode.INVALID_CART, "user is required");
            }
            if (store == null) {
                throw new CustomException(CartErrorCode.INVALID_CART, "store is required");
            }
            return new Cart(user, store);
        }
    }

    /* ------ 도메인 로직 ------- */

    public void addItem(CartItem item) {
        if (item == null) {
            throw new CustomException(CartErrorCode.INVALID_CART, "cart item cannot be null");
        }
        this.items.add(item);
    }

    public void removeItem(CartItem item) {
        if (!this.items.remove(item)) {
            throw new CustomException(CartErrorCode.CART_ITEM_NOT_FOUND, "cart item not found");
        }
    }

    public CartItem findItemByMenuId(Long menuId) {
        return items.stream()
                .filter(i -> i.getMenu().getId().equals(menuId))
                .findFirst()
                .orElse(null);
    }

    public void changeStore(Store newStore) {
        if (newStore == null) {
            throw new CustomException(CartErrorCode.INVALID_CART, "store cannot be null");
        }
        if (!items.isEmpty()) {
            throw new CustomException(CartErrorCode.CART_STORE_MISMATCH, "cannot change store when cart has items");
        }
        this.store = newStore;
    }

    public void clearItems() {
        items.clear();
    }

    public int getTotalPrice() {
        return items.stream()
                .mapToInt(CartItem::getTotalPrice)
                .sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
