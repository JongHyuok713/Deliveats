package com.example.deliveats.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank String password,
        String address,
        String phone
        ) {}
