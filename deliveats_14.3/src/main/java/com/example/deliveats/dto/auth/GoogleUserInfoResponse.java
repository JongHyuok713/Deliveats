package com.example.deliveats.dto.auth;

public record GoogleUserInfoResponse(
        String id,
        String email,
        String name,
        String picture,
        String locale
) {}
