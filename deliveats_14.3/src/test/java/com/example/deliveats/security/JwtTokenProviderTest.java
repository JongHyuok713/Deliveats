package com.example.deliveats.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtTokenProviderTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    void 액세스_토큰_생성과_검증이_정상작동한다() {
        // given
        String username = "test@test.com";
        String role = "ROLE_USER";

        // when
        String token = jwtTokenProvider.createAccessToken(username, role);

        // then
        assertThat(jwtTokenProvider.validate(token)).isTrue();
        assertThat(jwtTokenProvider.getUsername(token)).isEqualTo(username);
        assertThat(jwtTokenProvider.getRole(token)).isEqualTo(role);
    }

    @Test
    void 잘못된_토큰은_validate가_false를_반환한다() {
        String invalidToken = "invalid.token.value";
        assertThat(jwtTokenProvider.validate(invalidToken)).isFalse();
    }
}
