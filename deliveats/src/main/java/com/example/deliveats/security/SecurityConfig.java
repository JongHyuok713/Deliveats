package com.example.deliveats.security;

import com.example.deliveats.security.oauth.CustomOAuth2UserService;
import com.example.deliveats.security.oauth.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    @Order(1) // API 전용 체인: /api/**
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 인증/회원가입 API
                        .requestMatchers("/api/auth/login", "/api/auth/refresh", "/api/auth/logout", "/api/auth/register/**").permitAll()

                        // 내 정보 조회 인증
                        .requestMatchers("/api/users/me").authenticated()

                        // USER
                        .requestMatchers("/api/cart/**").hasAnyRole("USER", "OWNER", "ADMIN")
                        .requestMatchers("/api/orders/**").hasRole("USER")

                        // OWNER
                        .requestMatchers("/api/stores/**").hasRole("OWNER")

                        // RIDER
                        .requestMatchers("/api/rider/**").hasRole("RIDER")

                        // ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain viewFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/home", "/login", "/register",
                                "/oauth2/**", "/login/oauth2/**",
                                "/css/**", "/js/**", "/images/**", "/favicon.ico", "/webjars/**"
                        ).permitAll()

                        // 점주 가게 메뉴 수정
                        .requestMatchers("/stores/*/menu/*/edit").hasRole("OWNER")
                        .requestMatchers("/stores/**").permitAll()

                        // 주문/프로필 페이지
                        .requestMatchers("/profile").authenticated()
                        .requestMatchers("/orders/**").authenticated()

                        .anyRequest().authenticated()
                )

                // View는 세션 사용 (STATELESS 금지)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                // 일반 로그인
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )

                // OAuth2 로그인
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
