package com.example.deliveats.controller.order;

import com.example.deliveats.domain.order.Order;
import com.example.deliveats.domain.order.OrderStatus;
import com.example.deliveats.dto.order.OrderResponse;
import com.example.deliveats.security.CustomUserDetails;
import com.example.deliveats.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // 주문 생성 API (사용자 전용)
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Order order = orderService.createFromCart(userDetails.getUser());
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    // 내 주문 목록 조회
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my")
    public ResponseEntity<Page<Order>> myOrders(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @RequestParam(required = false) OrderStatus status,
                                                Pageable pageable) {
        return ResponseEntity.ok(orderService.getMyOrders(userDetails.getUser(), status, pageable));
    }

    // 주문 취소
    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) {
        orderService.cancelOrder(userDetails.getUser(), id);
        return ResponseEntity.noContent().build();
    }
}
