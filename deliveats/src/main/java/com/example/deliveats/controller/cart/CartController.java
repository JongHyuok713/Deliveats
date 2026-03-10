package com.example.deliveats.controller.cart;

import com.example.deliveats.dto.cart.CartAddItemRequest;
import com.example.deliveats.dto.cart.CartResponse;
import com.example.deliveats.security.CustomUserDetails;
import com.example.deliveats.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    // 장바구니 생성
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/items")
    public ResponseEntity<CartResponse> getMyCart(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                cartService.getMyCart(userDetails.getUser())
        );
    }

    // 장바구니 메뉴 추가
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CartAddItemRequest req
    ) {
        return ResponseEntity.ok(
                cartService.addItem(
                        userDetails.getUser(),
                        req.menuId(),
                        req.quantity()
                )
        );
    }

    // 장바구니 메뉴 수량 변경
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId,
            @RequestBody CartAddItemRequest req
    ) {
        return ResponseEntity.ok(cartService.updateQuantity(userDetails.getUser(), itemId, req.quantity()));
    }

    // 장바구니 메뉴 삭제
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(cartService.removeItem(userDetails.getUser(), itemId));
    }

    // 장바구니 비우기
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.clearCart(userDetails.getUser());
        return ResponseEntity.ok().build();
    }
}
