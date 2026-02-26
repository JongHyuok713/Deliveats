package com.example.deliveats.security.oauth;

import com.example.deliveats.domain.user.AuthProvider;

public interface OAuth2UserInfo {
    AuthProvider getProvider();
    String getProviderId();
    String getEmail();
    String getName();
}
