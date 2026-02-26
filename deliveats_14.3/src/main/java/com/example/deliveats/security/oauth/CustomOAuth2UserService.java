package com.example.deliveats.security.oauth;

import com.example.deliveats.domain.user.Role;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User rawUser = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = toUserInfo(registrationId, rawUser);

        if (userInfo.getEmail() == null || userInfo.getProviderId() == null) {
            throw new OAuth2AuthenticationException("OAUTH_USERINFO_ERROR");
        }

        User user = userRepository
                .findByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId())
                .orElseGet(() -> createOAuthUser(userInfo));

        return new CustomOAuth2User(user, rawUser.getAttributes());
    }

    private OAuth2UserInfo toUserInfo(String registrationId, OAuth2User rawUser) {
        if ("google".equalsIgnoreCase(registrationId)) {
            return new GoogleOAuth2UserInfo(rawUser.getAttributes());
        }
        throw new OAuth2AuthenticationException("UNSUPPORTED_OAUTH_PROVIDER: " + registrationId);
    }

    private User createOAuthUser(OAuth2UserInfo info) {
        return userRepository.save(User.builder()
                .username(info.getEmail())
                .password("{noop}OAUTH2_USER")
                .role(Role.USER)
                .provider(info.getProvider())
                .providerId(info.getProviderId())
                .name(info.getName())
                .build());
    }
}
