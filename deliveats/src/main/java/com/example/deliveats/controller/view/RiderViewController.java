package com.example.deliveats.controller.view;

import com.example.deliveats.domain.delivery.Delivery;
import com.example.deliveats.domain.delivery.DeliveryStatus;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.service.delivery.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/rider")
@PreAuthorize("hasRole('RIDER')")
public class RiderViewController {

    private final DeliveryService deliveryService;

    // 내 배달 목록 페이지
    @GetMapping("/deliveries")
    public String myDeliveries(@AuthenticationPrincipal User rider, Model model) {
        List<Delivery> deliveries = deliveryService.getMyDeliveries(rider);
        model.addAttribute("deliveries", deliveries);
        model.addAttribute("statuses", DeliveryStatus.values());
        model.addAttribute("title", "내 배달 목록");
        return "rider-delivery-list";
    }

    // 배달 상태 변경 처리
    @PostMapping("/deliveries/{id}/update")
    public String updateDeliveryStatus(@PathVariable Long id,
                                       @RequestParam("status") DeliveryStatus status,
                                       @AuthenticationPrincipal User rider,
                                       Model model) {
        try {
            deliveryService.updateStatus(id, status);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/rider/deliveries";
    }
}
