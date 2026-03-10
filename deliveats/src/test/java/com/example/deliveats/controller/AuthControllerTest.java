package com.example.deliveats.controller;

import com.example.deliveats.controller.auth.AuthController;
import com.example.deliveats.dto.auth.LoginRequest;
import com.example.deliveats.dto.auth.TokenResponse;
import com.example.deliveats.security.JwtAuthenticationFilter;
import com.example.deliveats.security.JwtTokenProvider;
import com.example.deliveats.service.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtTokenProvider.class)
        }
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    AuthService authService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void 로그인_성공시_JWT_토큰을_반환한다() throws Exception {
        //given
        LoginRequest request = new LoginRequest("test@test.com", "1234");
        TokenResponse tokenResponse = new TokenResponse("mock-access-token", "mock-refresh-token");

        Mockito.when(authService.login(any(LoginRequest.class)))
                .thenReturn(tokenResponse);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"));
    }

    @Test
    void 로그인_실패시_401을_반환한다() throws Exception {
        // given
        LoginRequest request = new LoginRequest("test@test.com", "wrong");

        Mockito.when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("INVALID"));

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
