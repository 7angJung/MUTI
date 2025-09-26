package com.muti.muti.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muti.muti.auth.dto.AuthRequest;
import com.muti.muti.auth.dto.AuthResponse;
import com.muti.muti.user.domain.User;
import com.muti.muti.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User testUser = new User("testuser", passwordEncoder.encode("password123"), "test@example.com");
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("로그인 성공 시 JWT 토큰을 반환한다")
    void login_success_returns_jwt() throws Exception {
        AuthRequest authRequest = new AuthRequest("testuser", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").exists());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_fail_wrong_password() throws Exception {
        AuthRequest authRequest = new AuthRequest("testuser", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("보호된 엔드포인트 접근 - 인증되지 않은 사용자")
    void access_secured_endpoint_unauthenticated() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("보호된 엔드포인트 접근 - JWT 토큰으로 인증된 사용자")
    void access_secured_endpoint_authenticated_with_jwt() throws Exception {
        // 1. 로그인하여 JWT 토큰 획득
        AuthRequest authRequest = new AuthRequest("testuser", "password123");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseString, AuthResponse.class);
        String jwt = authResponse.getJwt();

        // 2. 획득한 JWT를 헤더에 담아 보호된 엔드포인트에 요청
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(content().string("현재 로그인된 사용자 ID: testuser"));
    }
}
