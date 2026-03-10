package com.example.deliveats.dto.user;

import com.example.deliveats.domain.user.User;

public record UserProfileResponse(Long id,
                                  String username,
                                  String role,
                                  String address,
                                  String phone
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getAddress(),
                user.getPhone()
                );
    }
}
