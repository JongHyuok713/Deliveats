package com.example.deliveats.security;

import com.example.deliveats.domain.user.Role;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.repository.user.UserRepository;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class JwtAuthenticationFilterTest {

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupUser() {
        userRepository.deleteAll(); // 중복 제거

        // JWT가 인증 객체를 만들기 위해 필요한 유저 생성
        User user = User.builder()
                .username("test@test.com")
                .password(passwordEncoder.encode("1234"))
                .role(Role.USER)
                .address("서울시 테스트")
                .phone("01012345678")
                .build();

        userRepository.save(user);
    }

    // 테스트 간 인증정보가 남지 않도록 초기화
    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 유효한_JWT를_보내면_SecurityContext에_Authentication이_설정된다() throws ServletException, IOException {
        // given
        String token = jwtTokenProvider.createAccessToken("test@test.com", "ROLE_USER");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/secure"); // 필터가 적용되는 경로
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        MockFilterChain chain = new MockFilterChain();

        // when
        jwtAuthenticationFilter.doFilter(request, response, chain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("test@test.com");
    }

    @Test
    void 잘못된_JWT면_Authentication이_null이다() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/secure");
        request.addHeader("Authorization", "Bearer invalid.invalid.invalid");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // when
        jwtAuthenticationFilter.doFilter(request, response, chain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
    }

    @Test
    void Authorization_헤더가_없으면_Authentication이_null이다() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/secure");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // when
        jwtAuthenticationFilter.doFilter(request, response, chain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
    }

    @Test
    void shouldNotFilter_등록된_경로들은_필터가_건너뛴다() throws Exception {
        // given: 필터 예외 경로 (EXCLUDE_PREFIXES)
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/login");

        // when
        boolean skip = jwtAuthenticationFilter.shouldNotFilter(request);

        // then
        assertThat(skip).isTrue();
    }

    @Test
    void shouldNotFilter_루트경로는_필터_미적용() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/");

        boolean skip = jwtAuthenticationFilter.shouldNotFilter(request);

        assertThat(skip).isTrue();
    }
}
