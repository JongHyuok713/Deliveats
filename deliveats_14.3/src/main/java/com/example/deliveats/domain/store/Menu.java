package com.example.deliveats.domain.store;

import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.MenuErrorCode;
import com.example.deliveats.exception.StoreErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column
    private String imageUrl;

    @Column(nullable = false)
    private boolean isAvailable;

    protected Menu(
            String name,
            int price,
            Store store,
            String imageUrl,
            boolean isAvailable
    ) {
        this.name = name;
        this.price = price;
        this.store = store;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private int price;
        private Store store;
        private String imageUrl;
        private boolean isAvailable = true;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder price(int price) {
            this.price = price;
            return this;
        }

        public Builder store(Store store) {
            this.store = store;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder available(boolean available) {
            this.isAvailable = available;
            return this;
        }

        public Menu build() {
            if (name == null || name.isBlank())
                throw new CustomException(MenuErrorCode.INVALID_MENU,"menu name is required");
            if (store == null)
                throw new CustomException(MenuErrorCode.INVALID_MENU,"store is required");

            Menu menu = new Menu(name, price, store, imageUrl, isAvailable);
            store.addMenu(menu);
            return menu;
        }
    }

    public void updateInfo(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void disable() {
        this.isAvailable = false;
    }

    public void enable() {
        this.isAvailable = true;
    }
}
