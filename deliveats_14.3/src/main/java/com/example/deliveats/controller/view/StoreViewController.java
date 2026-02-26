package com.example.deliveats.controller.view;

import com.example.deliveats.dto.store.StoreListResponse;
import com.example.deliveats.security.CustomUserDetails;
import com.example.deliveats.service.review.ReviewService;
import com.example.deliveats.service.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StoreViewController {

    private final StoreService storeService;
    private final ReviewService reviewService;

    // 가게 상세보기 페이지
    @GetMapping("/stores/{storeId}")
    public String storeDetail(@PathVariable Long storeId, Model model) {
        model.addAttribute("store", storeService.findById(storeId));
        return "store/store-detail";
    }

    // 가게 등록 페이지
    @GetMapping("/stores/register")
    public String registerStorePage() {
        return "owner/owner-store-form";
    }

    // 가게 목록
    @GetMapping("/stores")
    public String storeListPage(@RequestParam(required = false) String q, Model model) {
        List<StoreListResponse> stores = storeService.getStoreList();

        if (q != null && !q.isBlank()) {
            stores = stores.stream().filter(s -> s.name().contains(q)).toList();
        }
        model.addAttribute("stores", stores);
        return "store/store-list";
    }

    // 내 가게 관리 페이지
    @GetMapping("/stores/my")
    public String myStorePage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        return "owner/my-store";
    }

    // 가게 수정 페이지
    @GetMapping("/stores/{storeId}/edit")
    public String editStorePage(@PathVariable Long storeId, Model model) {
        model.addAttribute("store", storeService.findById(storeId));
        return "store/store-edit";
    }

    // 메뉴 등록 페이지
    @GetMapping("/stores/{storeId}/menu/register")
    public String menuRegisterPage(@PathVariable Long storeId, Model model) {
        model.addAttribute("storeId", storeId);
        return "owner/owner-menu-form";
    }

    // 메뉴 수정 페이지
    @GetMapping("/stores/{storeId}/menu/{menuId}/edit")
    public String menuEditPage(
            @PathVariable Long storeId,
            @PathVariable Long menuId,
            Model model
    ) {
        model.addAttribute("storeId", storeId);
        model.addAttribute("menuId", menuId);
        return "store/menu-edit";
    }

}
