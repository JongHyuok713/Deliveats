package com.example.deliveats.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    SecurityFilterChain securityFilterChain;

    @Test // SecurityFilterChain Bean이 잘 생성되는지 확인
    void securityFilterChain_빈이_생성된다() {
        assert(securityFilterChain != null);
    }

    @Test // /api/auth/** 는 인증 필요 없음
    void api_auth는_인증없이_접근가능하다() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().isOk());
    }

    @Test // GET /api/stores/** 는 인증 없이 접근
    void api_stores_GET은_인증없이_허용된다() throws Exception {
        mockMvc.perform(get("/api/stores/1"))
                .andExpect(status().isOk());
    }

    @Test // POST /api/stores/** 는 OWNER 권한 필요 → 인증 없으면 403
    void api_stores_POST는_OWNER권한없으면_403() throws Exception {
        mockMvc.perform(post("/api/stores")
                .content("{}")
                .contentType("application/json"))
                .andExpect(status().isForbidden());
    }

    @Test // /api/users/me 는 인증 필요 → 인증 없으면 403
    void api_users_me는_인증필요_인증없으면_403() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test // 기타 요청은 인증 필요 → 403
    void 인증이_없는상태에서_일반요청은_403() throws Exception {
        mockMvc.perform(get("/secure-data"))
                .andExpect(status().isForbidden());
    }
}
