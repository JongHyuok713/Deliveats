package com.example.deliveats.controller.user;

import com.example.deliveats.domain.user.User;
import com.example.deliveats.dto.user.AddressUpdateRequest;
import com.example.deliveats.dto.user.PasswordUpdateRequest;
import com.example.deliveats.dto.user.PhoneUpdateRequest;
import com.example.deliveats.dto.user.UserProfileResponse;
import com.example.deliveats.security.CustomUserDetails;
import com.example.deliveats.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 로그인한 사용자 정보 조회
    @PreAuthorize(("isAuthenticated()"))
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(UserProfileResponse.from(userDetails.getUser()));
    }

    // 주소 수정
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/me/address")
    public ResponseEntity<Void> updateAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestBody AddressUpdateRequest request) {
        userService.updateAddress(userDetails.getUser(), request.address());
        return ResponseEntity.ok().build();
    }

    // 전화번호 수정
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/me/phone")
    public ResponseEntity<Void> updatePhone(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @RequestBody PhoneUpdateRequest request) {
        userService.updatePhone(userDetails.getUser(), request.phone());
        return ResponseEntity.ok().build();
    }

    // 비밀번호 수정
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestBody PasswordUpdateRequest request) {
        userService.updatePassword(userDetails.getUser(), request.currentPassword(), request.newPassword());
        return ResponseEntity.ok().build();
    }
}