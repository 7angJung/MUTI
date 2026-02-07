package com.muti.domain.survey.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muti.domain.survey.dto.request.SubmitSurveyRequest;
import com.muti.domain.survey.dto.request.SurveyAnswerDto;
import com.muti.global.common.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MUTI Survey API E2E 통합 테스트
 * 실제 API 엔드포인트를 호출하여 전체 플로우 검증
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
@DisplayName("Survey API E2E 테스트")
class SurveyApiE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("E2E: 헬스체크 - API 정상 작동 확인")
    void e2e_Healthcheck() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/surveys/ping"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("pong"));
    }

    @Test
    @DisplayName("E2E: 설문 목록 조회 → 설문 상세 조회")
    void e2e_GetSurveys_Then_GetDetail() throws Exception {
        // Step 1: 설문 목록 조회
        MvcResult listResult = mockMvc.perform(get("/api/v1/surveys"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].title").exists())
                .andExpect(jsonPath("$.data[0].active").value(true))
                .andReturn();

        // Step 2: 첫 번째 설문의 ID 추출
        String listResponse = listResult.getResponse().getContentAsString();
        Long surveyId = objectMapper.readTree(listResponse)
                .get("data").get(0).get("id").asLong();

        // Step 3: 설문 상세 조회
        mockMvc.perform(get("/api/v1/surveys/" + surveyId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(surveyId))
                .andExpect(jsonPath("$.data.title").value("MUTI 음악 성향 테스트"))
                .andExpect(jsonPath("$.data.questions").isArray())
                .andExpect(jsonPath("$.data.questions.length()").value(8))
                .andExpect(jsonPath("$.data.questions[0].options").isArray())
                .andExpect(jsonPath("$.data.questions[0].options.length()").value(2));
    }

    @Test
    @DisplayName("E2E: 전체 플로우 - 설문 조회 → 응답 제출 → ESAP 타입 산출")
    void e2e_FullFlow_ESAP() throws Exception {
        // Step 1: 설문 목록 조회
        MvcResult listResult = mockMvc.perform(get("/api/v1/surveys"))
                .andExpect(status().isOk())
                .andReturn();

        Long surveyId = objectMapper.readTree(listResult.getResponse().getContentAsString())
                .get("data").get(0).get("id").asLong();

        // Step 2: 설문 상세 조회 (질문 ID 수집)
        MvcResult detailResult = mockMvc.perform(get("/api/v1/surveys/" + surveyId))
                .andExpect(status().isOk())
                .andReturn();

        var questionsNode = objectMapper.readTree(detailResult.getResponse().getContentAsString())
                .get("data").get("questions");

        // Step 3: 모든 질문에 대해 첫 번째 옵션(E, S, A, P 방향) 선택
        List<SurveyAnswerDto> answers = new java.util.ArrayList<>();
        for (int i = 0; i < questionsNode.size(); i++) {
            var question = questionsNode.get(i);
            Long questionId = question.get("id").asLong();
            Long firstOptionId = question.get("options").get(0).get("id").asLong();
            answers.add(new SurveyAnswerDto(questionId, firstOptionId));
        }

        // Step 4: 설문 응답 제출
        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(surveyId)
                .answers(answers)
                .sessionId("e2e-test-session")
                .build();

        MvcResult submitResult = mockMvc.perform(post("/api/v1/surveys/" + surveyId + "/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.mutiType").value("ESAP"))
                .andExpect(jsonPath("$.data.mutiTypeName").exists())
                .andExpect(jsonPath("$.data.axisScores").exists())
                .andExpect(jsonPath("$.data.axisScores.E_I").isNumber())
                .andExpect(jsonPath("$.data.axisScores.S_F").isNumber())
                .andExpect(jsonPath("$.data.axisScores.A_D").isNumber())
                .andExpect(jsonPath("$.data.axisScores.P_U").isNumber())
                .andExpect(jsonPath("$.message").value("MUTI 타입이 성공적으로 산출되었습니다."))
                .andReturn();

        // Step 5: 결과 검증 - 모든 축 점수가 양수여야 함
        var resultNode = objectMapper.readTree(submitResult.getResponse().getContentAsString())
                .get("data");

        int eiScore = resultNode.get("axisScores").get("E_I").asInt();
        int sfScore = resultNode.get("axisScores").get("S_F").asInt();
        int adScore = resultNode.get("axisScores").get("A_D").asInt();
        int puScore = resultNode.get("axisScores").get("P_U").asInt();

        assertThat(eiScore).isPositive();
        assertThat(sfScore).isPositive();
        assertThat(adScore).isPositive();
        assertThat(puScore).isPositive();
    }

    @Test
    @DisplayName("E2E: 전체 플로우 - 설문 조회 → 응답 제출 → IFDU 타입 산출")
    void e2e_FullFlow_IFDU() throws Exception {
        // Step 1: 설문 목록 조회
        MvcResult listResult = mockMvc.perform(get("/api/v1/surveys"))
                .andExpect(status().isOk())
                .andReturn();

        Long surveyId = objectMapper.readTree(listResult.getResponse().getContentAsString())
                .get("data").get(0).get("id").asLong();

        // Step 2: 설문 상세 조회
        MvcResult detailResult = mockMvc.perform(get("/api/v1/surveys/" + surveyId))
                .andExpect(status().isOk())
                .andReturn();

        var questionsNode = objectMapper.readTree(detailResult.getResponse().getContentAsString())
                .get("data").get("questions");

        // Step 3: 모든 질문에 대해 두 번째 옵션(I, F, D, U 방향) 선택
        List<SurveyAnswerDto> answers = new java.util.ArrayList<>();
        for (int i = 0; i < questionsNode.size(); i++) {
            var question = questionsNode.get(i);
            Long questionId = question.get("id").asLong();
            Long secondOptionId = question.get("options").get(1).get("id").asLong();
            answers.add(new SurveyAnswerDto(questionId, secondOptionId));
        }

        // Step 4: 설문 응답 제출
        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(surveyId)
                .answers(answers)
                .sessionId("e2e-test-session-2")
                .build();

        MvcResult submitResult = mockMvc.perform(post("/api/v1/surveys/" + surveyId + "/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.mutiType").value("IFDU"))
                .andExpect(jsonPath("$.message").value("MUTI 타입이 성공적으로 산출되었습니다."))
                .andReturn();

        // Step 5: 결과 검증 - 모든 축 점수가 음수여야 함
        var resultNode = objectMapper.readTree(submitResult.getResponse().getContentAsString())
                .get("data");

        int eiScore = resultNode.get("axisScores").get("E_I").asInt();
        int sfScore = resultNode.get("axisScores").get("S_F").asInt();
        int adScore = resultNode.get("axisScores").get("A_D").asInt();
        int puScore = resultNode.get("axisScores").get("P_U").asInt();

        assertThat(eiScore).isNegative();
        assertThat(sfScore).isNegative();
        assertThat(adScore).isNegative();
        assertThat(puScore).isNegative();
    }

    @Test
    @DisplayName("E2E: 실패 케이스 - 존재하지 않는 설문 조회")
    void e2e_GetNonExistentSurvey() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/surveys/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("S001"));
    }

    @Test
    @DisplayName("E2E: 실패 케이스 - 불완전한 응답 제출")
    void e2e_SubmitIncompleteAnswers() throws Exception {
        // given
        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(1L)
                .answers(List.of(
                        new SurveyAnswerDto(1L, 1L) // 8개 중 1개만
                ))
                .sessionId("incomplete-test")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/surveys/1/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("S003"));
    }

    @Test
    @DisplayName("E2E: 실패 케이스 - 빈 응답 제출")
    void e2e_SubmitEmptyAnswers() throws Exception {
        // given
        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(1L)
                .answers(List.of()) // 빈 응답
                .sessionId("empty-test")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/surveys/1/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("E2E: 통합 시나리오 - 여러 사용자의 설문 응답")
    void e2e_MultipleUsers() throws Exception {
        // 사용자 1: ESAP 타입
        SubmitSurveyRequest user1Request = SubmitSurveyRequest.builder()
                .surveyId(1L)
                .answers(List.of(
                        new SurveyAnswerDto(1L, 1L),
                        new SurveyAnswerDto(2L, 3L),
                        new SurveyAnswerDto(3L, 5L),
                        new SurveyAnswerDto(4L, 7L),
                        new SurveyAnswerDto(5L, 9L),
                        new SurveyAnswerDto(6L, 11L),
                        new SurveyAnswerDto(7L, 13L),
                        new SurveyAnswerDto(8L, 15L)
                ))
                .sessionId("user-1-session")
                .build();

        mockMvc.perform(post("/api/v1/surveys/1/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1Request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.mutiType").value("ESAP"));

        // 사용자 2: IFDU 타입
        SubmitSurveyRequest user2Request = SubmitSurveyRequest.builder()
                .surveyId(1L)
                .answers(List.of(
                        new SurveyAnswerDto(1L, 2L),
                        new SurveyAnswerDto(2L, 4L),
                        new SurveyAnswerDto(3L, 6L),
                        new SurveyAnswerDto(4L, 8L),
                        new SurveyAnswerDto(5L, 10L),
                        new SurveyAnswerDto(6L, 12L),
                        new SurveyAnswerDto(7L, 14L),
                        new SurveyAnswerDto(8L, 16L)
                ))
                .sessionId("user-2-session")
                .build();

        mockMvc.perform(post("/api/v1/surveys/1/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2Request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.mutiType").value("IFDU"));
    }
}