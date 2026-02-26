package com.example.deliveats.controller.view;

import com.example.deliveats.domain.order.Order;
import com.example.deliveats.security.CustomUserDetails;
import com.example.deliveats.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class OrderViewController {

    private final OrderService orderService;

    // 주문 확인 페이지
    @GetMapping("/orders/confirm")
    public String orderConfirmPage() {
        return "order/order-confirm";
    }

    // 주문 상세 페이지
    @GetMapping("/orders/{orderId}")
    public String orderDetail(@PathVariable Long orderId,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              Model model) {
        Order order = orderService.findOrderForUser(orderId, userDetails.getUser());
        model.addAttribute("order", order);
        return "order/order-detail";
    }

    // 내 주문 리스트
    @GetMapping("/orders/my")
    public String orderList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable,
            Model model
    ) {
        var orders = orderService.getMyOrders(userDetails.getUser(), null, pageable);
        model.addAttribute("orders", orders);
        return "order/order-list";
    }
}
