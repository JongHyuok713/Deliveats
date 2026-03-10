package com.example.deliveats.domain.store;

import com.example.deliveats.domain.user.User;
import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.StoreErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stores")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private int minOrderPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column
    private String mainImage;

    @Column(nullable = false)
    private Double ratingAvg = 0.0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>();

    protected Store(
            String name,
            String category,
            String address,
            int minOrderPrice,
            User owner,
            String mainImage
    ) {
        this.name = name;
        this.category = category;
        this.address = address;
        this.minOrderPrice = minOrderPrice;
        this.owner = owner;
        this.mainImage = mainImage;
        this.ratingAvg = 0.0;
        this.createdAt = LocalDateTime.now();
        this.menus = new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String category;
        private String address;
        private int minOrderPrice;
        private User owner;
        private String mainImage;

        private Builder() {}

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder minOrderPrice(int minOrderPrice) {
            this.minOrderPrice = minOrderPrice;
            return this;
        }

        public Builder owner(User owner) {
            this.owner = owner;
            return this;
        }

        public Builder mainImage(String mainImage) {
            this.mainImage = mainImage;
            return this;
        }

        public Store build() {
            if (name == null || name.isBlank())
                throw new CustomException(StoreErrorCode.INVALID_STORE,"store name is required");
            if (owner == null)
                throw new CustomException(StoreErrorCode.INVALID_STORE,"owner is required");

            return new Store(
                    name,
                    category,
                    address,
                    minOrderPrice,
                    owner,
                    mainImage
            );
        }
    }

    protected void addMenu(Menu menu) {
        menus.add(menu);
    }

    protected void removeMenu(Menu menu) {
        menus.remove(menu);
    }

    public void updateMainImage(String imageUrl) {
        this.mainImage = imageUrl;
    }

    public void updateInfo(String name, String category, String address, int minOrderPrice) {
        this.name = name;
        this.category = category;
        this.address = address;
        this.minOrderPrice = minOrderPrice;
    }
}
