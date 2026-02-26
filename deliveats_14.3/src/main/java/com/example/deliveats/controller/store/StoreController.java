package com.example.deliveats.controller.store;

import com.example.deliveats.domain.store.Store;
import com.example.deliveats.dto.review.ReviewResponse;
import com.example.deliveats.dto.store.*;
import com.example.deliveats.dto.store.StoreCreateRequest.MenuCreateRequest;
import com.example.deliveats.security.CustomUserDetails;
import com.example.deliveats.service.menu.MenuService;
import com.example.deliveats.service.review.ReviewService;
import com.example.deliveats.service.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;
    private final MenuService menuService;
    private final ReviewService reviewService;

    // 단일 가게 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<StoreResponse> detail(@PathVariable Long id) {
        Store store = storeService.findById(id);
        return ResponseEntity.ok(storeService.toStoreResponse(store));
    }

    // 가게 등록 (OWNER 전용)
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/register")
    public ResponseEntity<?> registerStore(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestBody StoreCreateRequest req
    ) {
        return ResponseEntity.ok(storeService.createStore(
                userDetails.getUser(),
                req.name(),
                req.category(),
                req.address(),
                req.minOrderPrice()
        ));
    }

    // 가게 정보 수정
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{storeId}")
    public ResponseEntity<?> updateStore(
            @PathVariable Long storeId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody StoreUpdateRequest req
    ) {
        storeService.updateStoreInfo(storeId, req, userDetails.getUser());
        return ResponseEntity.ok("UPDATED");
    }

    // 메뉴 등록 (OWNER 전용)
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/{storeId}/menus")
    public ResponseEntity<?> addMenu(@AuthenticationPrincipal CustomUserDetails userDetails,
                                     @PathVariable Long storeId,
                                     @RequestBody MenuCreateRequest req
    ) {
        return ResponseEntity.ok(menuService.addMenu(
                storeId,
                userDetails.getUser(),
                req.name(),
                req.price(),
                req.imageUrl()
        ));
    }

    // 메뉴 수정 (OWNER 전용)
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{storeId}/menus/{menuId}")
    public ResponseEntity<?> updateMenu(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long storeId,
            @PathVariable Long menuId,
            @RequestBody MenuUpdateRequest req
    ) {
        return ResponseEntity.ok(menuService.updateMenu(
                storeId,
                menuId,
                userDetails.getUser(),
                req.name(),
                req.price(),
                req.imageUrl(),
                req.isAvailable()
        ));
    }

    // 메뉴 삭제 (OWNER 전용)
    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{storeId}/menus/{menuId}")
    public ResponseEntity<?> deleteMenu(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long storeId,
            @PathVariable Long menuId
    ) {
        menuService.deleteMenu(storeId, menuId, userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    // 점주 확인
    @GetMapping("/{storeId}/owner")
    public ResponseEntity<?> getStoreOwner(@PathVariable Long storeId) {
        Store store = storeService.findById(storeId);
        return ResponseEntity.ok(Map.of("ownerId", store.getOwner().getId()));
    }

    // 점주 자신의 가게 정보 조회 + 메뉴 + 리뷰
    @GetMapping("/owner/stores/my")
    public ResponseEntity<?> getMyStore(@AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body("UNAUTHORIZED");
        }
        Store store = storeService.findOwnerStoreOrNull(userDetails.getUser().getId());
        if (store == null) {
            return ResponseEntity.status(404).body("STORE_NOT_FOUND");
        }

        List<MenuResponse> menus = store.getMenus()
                .stream().map(MenuResponse::from).toList();

        List<ReviewResponse> reviews = reviewService.getStoreReviews(store.getId())
                .stream().map(ReviewResponse::from).toList();

        MyStoreResponse res = MyStoreResponse.builder()
                .store(StoreResponse.from(store, menus))
                .menus(menus)
                .reviews(reviews)
                .build();

        return ResponseEntity.ok(res);
    }
}
