package com.example.deliveats.service.store;

import com.example.deliveats.domain.store.Store;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.dto.store.MenuResponse;
import com.example.deliveats.dto.store.StoreListResponse;
import com.example.deliveats.dto.store.StoreResponse;
import com.example.deliveats.dto.store.StoreUpdateRequest;
import com.example.deliveats.exception.*;
import com.example.deliveats.exception.store.StoreNotFoundException;
import com.example.deliveats.repository.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    // 가게 단일 조회
    public Store findById(Long id) {
        return storeRepository.findById(id).orElseThrow(StoreNotFoundException::new);
    }

    public Store findOwnerStoreOrNull(Long ownerId) {
        return storeRepository.findByOwnerId(ownerId).orElse(null);
    }

    // 점주 가게 조회
    public Store findByOwnerId(Long ownerId) {
        return storeRepository.findByOwnerId(ownerId).orElseThrow(StoreNotFoundException::new);
    }

    // 가게 등록 (점주 전용)
    @Transactional
    public Store createStore(User owner, String name, String category, String address, int minOrderPrice) {
        Store store = Store.builder()
                .owner(owner)
                .name(name)
                .category(category)
                .address(address)
                .minOrderPrice(minOrderPrice)
                .build();
        return storeRepository.save(store);
    }

    // 가게 수정 (본인 소유 가게만)
    @Transactional
    public void updateStoreInfo(Long storeId, StoreUpdateRequest req, User owner) {
        Store store = findById(storeId);

        if (!store.getOwner().getId().equals(owner.getId())) {
            throw new CustomException(StoreErrorCode.STORE_PERMISSION_DENIED);
        }

        store.updateInfo(
                req.name(),
                req.category(),
                req.address(),
                req.minOrderPrice()
        );

        // 대표 이미지 URL 업데이트
        if (req.mainImage() != null) {
            store.updateMainImage(req.mainImage());
        }
    }

    // 메뉴 리스트 포함한 전체 StoreListResponse 반환
    public List<StoreListResponse> getStoreList() {
        List<Store> stores = storeRepository.findAll();

        return stores.stream().map(store -> {
            String imageUrl = null;
            if (store.getMenus() != null && !store.getMenus().isEmpty()) {
                imageUrl = store.getMenus().get(0).getImageUrl();
            }

            return new StoreListResponse(
                    store.getId(),
                    store.getName(),
                    store.getCategory(),
                    store.getAddress(),
                    store.getMinOrderPrice(),
                    imageUrl
            );
        }).toList();
    }

    public StoreResponse toStoreResponse(Store store) {
        List<MenuResponse> menuList = store.getMenus()
                .stream().map(MenuResponse::from).toList();

        return StoreResponse.from(store, menuList);
    }

    @Transactional // 가게 삭제 (본인 소유만 가능)
    public void deleteStore(Long id, User owner) {
        Store store = storeRepository.findById(id)
                .orElseThrow(StoreNotFoundException::new);

        if (!store.getOwner().getId().equals(owner.getId())) {
            throw new CustomException(StoreErrorCode.STORE_PERMISSION_DENIED);
        }
        storeRepository.delete(store);
    }
}
