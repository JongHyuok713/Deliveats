package com.example.deliveats.security.oauth;

import com.example.deliveats.domain.user.Role;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.repository.user.UserRepository;
import com.example.deliveats.security.CookieTokenService;
import com.example.deliveats.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieTokenService cookieTokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {
        Object principal = authentication.getPrincipal();

        User user;
        if (principal instanceof CustomOAuth2User cu) {
            user = cu.getUser();
        } else if (principal instanceof OAuth2User oAuth2User) {
            String email = oAuth2User.getAttribute("email");
            if (email == null) throw new IllegalStateException("Google 계정에서 email을 가져올 수 없습니다.");

            user = userRepository.findByUsername(email)
                    .orElseGet(() -> userRepository.save(User.builder()
                            .username(email)
                            .password("{noop}OAUTH2_USER")
                            .role(Role.USER)
                            .build()));
        } else {
            throw new IllegalStateException("Unsupported principal type:" + principal.getClass());
        }
        String accessToken = jwtTokenProvider.createAccessToken(user.getUsername(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getName());

        boolean secure = request.isSecure();
        cookieTokenService.setRefreshTokenCookie(response, refreshToken, secure);

        response.sendRedirect("http://localhost:8081/oauth2/success#accessToken=" + accessToken);
    }
}
