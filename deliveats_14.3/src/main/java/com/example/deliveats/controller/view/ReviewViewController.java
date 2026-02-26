package com.example.deliveats.controller.view;

import com.example.deliveats.domain.user.User;
import com.example.deliveats.dto.review.ReviewForm;
import com.example.deliveats.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ReviewViewController {

    private final ReviewService reviewService;

    @GetMapping("/reviews/new/{orderId}")
    public String reviewForm(@PathVariable Long orderId, Model model) {
        ReviewForm form = new ReviewForm();
        form.setOrderId(orderId);
        model.addAttribute("reviewForm", form);
        model.addAttribute("title", "리뷰 작성");
        return "review-form";
    }

    @PostMapping("/reviews/create")
    public String submitReview(@AuthenticationPrincipal User user,
                               @Valid @ModelAttribute("reviewForm") ReviewForm form,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "리뷰 입력값이 잘못되었습니다.");
            return "review-form";
        }

        reviewService.createReview(user, form.getOrderId(), form.getRating(), form.getContent());
        return "redirect:/orders/my";
    }
}
