package com.example.deliveats.dto.user;

public record PasswordUpdateRequest(String currentPassword, String newPassword) {
}
