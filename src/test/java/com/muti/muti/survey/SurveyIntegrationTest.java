package com.muti.muti.survey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muti.muti.auth.dto.AuthRequest;
import com.muti.muti.auth.dto.AuthResponse;
import com.muti.muti.survey.domain.Question;
import com.muti.muti.survey.dto.SurveySubmissionDto;
import com.muti.muti.survey.repository.QuestionRepository;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class SurveyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwt;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        testUser = new User("surveyid", passwordEncoder.encode("password123"), "survey@example.com");
        userRepository.save(testUser);

        // Authenticate and get JWT
        AuthRequest authRequest = new AuthRequest("surveyid", "password123");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();
        String responseString = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseString, AuthResponse.class);
        jwt = authResponse.getJwt();
    }

    @Test
    @DisplayName("인증된 사용자는 설문 질문 목록을 조회할 수 있다")
    void get_survey_questions_authenticated() throws Exception {
        mockMvc.perform(get("/api/survey/questions")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].choices", hasSize(2)));
    }

    @Test
    @DisplayName("설문 답변을 제출하고 MUTI 결과를 받는다")
    void submit_survey_and_get_muti_result() throws Exception {
        // Assuming Question IDs are 1, 2, 3, 4 and Choice IDs are 1-8 in order
        // Let's choose I, F, A, P
        Map<Long, Long> answers = new HashMap<>();
        answers.put(1L, 2L); // Q1 -> Choice 2 (I)
        answers.put(2L, 4L); // Q2 -> Choice 4 (F)
        answers.put(3L, 5L); // Q3 -> Choice 5 (A)
        answers.put(4L, 7L); // Q4 -> Choice 7 (P)

        SurveySubmissionDto submission = new SurveySubmissionDto();
        submission.setAnswers(answers);

        mockMvc.perform(post("/api/survey/submit")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mutiType").value("IFAP"));

        // Verify the type was saved to the user
        User updatedUser = userRepository.findByUserId("surveyid").get();
        assertEquals("IFAP", updatedUser.getMutiType());
    }

    @Test
    @DisplayName("MUTI 타입에 맞는 음악을 추천받는다")
    void get_recommendations_after_survey() throws Exception {
        // 1. Submit survey to get MUTI type IFAP
        Map<Long, Long> answers = new HashMap<>();
        answers.put(1L, 2L); // I
        answers.put(2L, 4L); // F
        answers.put(3L, 5L); // A
        answers.put(4L, 7L); // P
        SurveySubmissionDto submission = new SurveySubmissionDto();
        submission.setAnswers(answers);

        mockMvc.perform(post("/api/survey/submit")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isOk());

        // 2. Get recommendations
        mockMvc.perform(get("/api/music/recommendations")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                // Check that genres are from the expected set
                .andExpect(jsonPath("$[0].genre").value(org.hamcrest.Matchers.oneOf("록", "재즈", "팝", "발라드", "댄스", "클래식", "인디")));

    }
}