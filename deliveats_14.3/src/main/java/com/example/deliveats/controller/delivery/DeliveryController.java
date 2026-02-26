package com.example.deliveats.controller.delivery;

import com.example.deliveats.domain.delivery.Delivery;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.dto.delivery.DeliveryAssignRequest;
import com.example.deliveats.dto.delivery.DeliveryStatusUpdateRequest;
import com.example.deliveats.security.CustomUserDetails;
import com.example.deliveats.service.delivery.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    // 배달 배정 (ADMIN 또는 OWNER 권한)
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @PostMapping("/assign")
    public ResponseEntity<Delivery> assignDelivery(
            @Valid @RequestBody DeliveryAssignRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(deliveryService.assignRider(request.orderId(), userDetails.getUser()));
    }

    // 배달 상태 변경 (RIDER 권한)
    @PreAuthorize("hasRole('RIDER')")
    @PatchMapping("/{deliveryId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long deliveryId,
            @Valid @RequestBody DeliveryStatusUpdateRequest request) {
        deliveryService.updateStatus(deliveryId, request.status());
        return ResponseEntity.noContent().build();
    }

    // 내 배달 목록 조회 (RIDER 전용)
    @PreAuthorize("hasRole('RIDER')")
    @GetMapping("/my")
    public ResponseEntity<List<Delivery>> gatMyDeliveries(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(deliveryService.getMyDeliveries(userDetails.getUser()));
    }

    // 전체 배달 목록 조회 (ADMIN 전용)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getDeliveriesByStatus(null));
    }
}
