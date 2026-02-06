package com.muti.domain.survey.controller;

import com.muti.global.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SurveyController 통합 테스트
 */
@WebMvcTest(SurveyController.class)
@Import(SecurityConfig.class)
@DisplayName("SurveyController 테스트")
class SurveyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/surveys/ping - 성공")
    void ping_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/surveys/ping"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("pong"))
                .andExpect(jsonPath("$.message").doesNotExist())
                .andExpect(jsonPath("$.errorCode").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/v1/surveys/ping - Content-Type JSON")
    void ping_ContentType() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/surveys/ping"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

}