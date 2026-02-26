package com.example.deliveats.controller.view;

import com.example.deliveats.repository.order.OrderRepository;
import com.example.deliveats.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminViewController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        long totalUsers = userRepository.count();
        long totalOrders = orderRepository.count();
        long completed = orderRepository.findAll().stream()
                .filter(o -> o.getStatus().name().equals("COMPLETED"))
                .count();

        model.addAttribute("userCount", totalUsers);
        model.addAttribute("totalSales", totalOrders * 20000);
        model.addAttribute("completedCount", completed);
        model.addAttribute("inProgressCount", totalOrders - completed);
        model.addAttribute("title", "관리자 대시보드");

        return "admin-dashboard";
    }
}
