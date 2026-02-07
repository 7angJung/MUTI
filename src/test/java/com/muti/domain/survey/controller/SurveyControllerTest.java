package com.muti.domain.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muti.domain.survey.dto.request.SubmitSurveyRequest;
import com.muti.domain.survey.dto.request.SurveyAnswerDto;
import com.muti.domain.survey.dto.response.SurveyDto;
import com.muti.domain.survey.dto.response.SurveyResultDto;
import com.muti.domain.survey.service.SurveyResponseService;
import com.muti.domain.survey.service.SurveyService;
import com.muti.global.config.SecurityConfig;
import com.muti.global.error.BusinessException;
import com.muti.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SurveyService surveyService;

    @MockBean
    private SurveyResponseService surveyResponseService;

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

    @Test
    @DisplayName("GET /api/v1/surveys - 활성 설문 목록 조회 성공")
    void getActiveSurveys_Success() throws Exception {
        // given
        SurveyDto survey1 = SurveyDto.builder()
                .id(1L)
                .title("MUTI 음악 성향 테스트")
                .description("당신의 음악 취향을 분석합니다")
                .active(true)
                .build();

        SurveyDto survey2 = SurveyDto.builder()
                .id(2L)
                .title("음악 성향 테스트 v2")
                .description("업데이트된 버전")
                .active(true)
                .build();

        given(surveyService.getActiveSurveys()).willReturn(List.of(survey1, survey2));

        // when & then
        mockMvc.perform(get("/api/v1/surveys"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("MUTI 음악 성향 테스트"))
                .andExpect(jsonPath("$.data[0].active").value(true))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].title").value("음악 성향 테스트 v2"));
    }

    @Test
    @DisplayName("GET /api/v1/surveys - 활성 설문 없음 (빈 리스트)")
    void getActiveSurveys_Empty() throws Exception {
        // given
        given(surveyService.getActiveSurveys()).willReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/v1/surveys"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/surveys/{surveyId} - 설문 상세 조회 성공")
    void getSurveyDetail_Success() throws Exception {
        // given
        SurveyDto surveyDto = SurveyDto.builder()
                .id(1L)
                .title("MUTI 음악 성향 테스트")
                .description("당신의 음악 취향을 분석합니다")
                .active(true)
                .build();

        given(surveyService.getSurveyDetail(1L)).willReturn(surveyDto);

        // when & then
        mockMvc.perform(get("/api/v1/surveys/{surveyId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("MUTI 음악 성향 테스트"))
                .andExpect(jsonPath("$.data.description").value("당신의 음악 취향을 분석합니다"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/surveys/{surveyId} - 존재하지 않는 설문 (실패)")
    void getSurveyDetail_NotFound() throws Exception {
        // given
        given(surveyService.getSurveyDetail(999L))
                .willThrow(new BusinessException(ErrorCode.SURVEY_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/surveys/{surveyId}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("S001"));
    }

    @Test
    @DisplayName("POST /api/v1/surveys/{surveyId}/submit - 설문 응답 제출 성공 (ESAP)")
    void submitSurvey_Success_ESAP() throws Exception {
        // given
        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(1L)
                .answers(List.of(
                        new SurveyAnswerDto(1L, 1L),
                        new SurveyAnswerDto(2L, 3L),
                        new SurveyAnswerDto(3L, 5L),
                        new SurveyAnswerDto(4L, 7L)
                ))
                .sessionId("test-session-123")
                .build();

        SurveyResultDto resultDto = SurveyResultDto.builder()
                .id(1L)
                .surveyId(1L)
                .mutiType("ESAP")
                .mutiTypeName("활발한 감성파")
                .mutiTypeDescription("밝고 활발한 음악을 선호하는 감성적인 성향")
                .axisScores(Map.of(
                        "E_I", 10,
                        "S_F", 8,
                        "A_D", 12,
                        "P_U", 6
                ))
                .axisDirections(Map.of(
                        "E_I", "E",
                        "S_F", "S",
                        "A_D", "A",
                        "P_U", "P"
                ))
                .build();

        given(surveyResponseService.submitSurvey(any(SubmitSurveyRequest.class)))
                .willReturn(resultDto);

        // when & then
        mockMvc.perform(post("/api/v1/surveys/{surveyId}/submit", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.mutiType").value("ESAP"))
                .andExpect(jsonPath("$.data.mutiTypeName").value("활발한 감성파"))
                .andExpect(jsonPath("$.data.axisScores.E_I").value(10))
                .andExpect(jsonPath("$.data.axisScores.S_F").value(8))
                .andExpect(jsonPath("$.data.axisScores.A_D").value(12))
                .andExpect(jsonPath("$.data.axisScores.P_U").value(6))
                .andExpect(jsonPath("$.message").value("MUTI 타입이 성공적으로 산출되었습니다."));
    }

    @Test
    @DisplayName("POST /api/v1/surveys/{surveyId}/submit - 설문 응답 제출 성공 (IFDU)")
    void submitSurvey_Success_IFDU() throws Exception {
        // given
        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(1L)
                .answers(List.of(
                        new SurveyAnswerDto(1L, 2L),
                        new SurveyAnswerDto(2L, 4L),
                        new SurveyAnswerDto(3L, 6L),
                        new SurveyAnswerDto(4L, 8L)
                ))
                .sessionId("test-session-456")
                .build();

        SurveyResultDto resultDto = SurveyResultDto.builder()
                .id(2L)
                .surveyId(1L)
                .mutiType("IFDU")
                .mutiTypeName("차분한 몽환파")
                .mutiTypeDescription("조용하고 몽환적인 음악을 선호하는 차분한 성향")
                .axisScores(Map.of(
                        "E_I", -8,
                        "S_F", -6,
                        "A_D", -10,
                        "P_U", -4
                ))
                .axisDirections(Map.of(
                        "E_I", "I",
                        "S_F", "F",
                        "A_D", "D",
                        "P_U", "U"
                ))
                .build();

        given(surveyResponseService.submitSurvey(any(SubmitSurveyRequest.class)))
                .willReturn(resultDto);

        // when & then
        mockMvc.perform(post("/api/v1/surveys/{surveyId}/submit", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.mutiType").value("IFDU"))
                .andExpect(jsonPath("$.data.mutiTypeName").value("차분한 몽환파"))
                .andExpect(jsonPath("$.data.axisScores.E_I").value(-8))
                .andExpect(jsonPath("$.data.axisScores.S_F").value(-6))
                .andExpect(jsonPath("$.data.axisScores.A_D").value(-10))
                .andExpect(jsonPath("$.data.axisScores.P_U").value(-4))
                .andExpect(jsonPath("$.message").value("MUTI 타입이 성공적으로 산출되었습니다."));
    }

    @Test
    @DisplayName("POST /api/v1/surveys/{surveyId}/submit - 비활성화된 설문 제출 (실패)")
    void submitSurvey_InactiveSurvey_Fail() throws Exception {
        // given
        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(1L)
                .answers(List.of(new SurveyAnswerDto(1L, 1L)))
                .build();

        given(surveyResponseService.submitSurvey(any(SubmitSurveyRequest.class)))
                .willThrow(new BusinessException(ErrorCode.INVALID_SURVEY_RESPONSE, "비활성화된 설문입니다."));

        // when & then
        mockMvc.perform(post("/api/v1/surveys/{surveyId}/submit", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("S003"));
    }

    @Test
    @DisplayName("POST /api/v1/surveys/{surveyId}/submit - 응답 없이 제출 (실패)")
    void submitSurvey_NoAnswers_Fail() throws Exception {
        // given
        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(1L)
                .answers(List.of()) // 빈 응답
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/surveys/{surveyId}/submit", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/surveys/{surveyId}/submit - 응답 수 불일치 (실패)")
    void submitSurvey_AnswerCountMismatch_Fail() throws Exception {
        // given
        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(1L)
                .answers(List.of(
                        new SurveyAnswerDto(1L, 1L) // 1개만
                ))
                .build();

        given(surveyResponseService.submitSurvey(any(SubmitSurveyRequest.class)))
                .willThrow(new BusinessException(ErrorCode.INVALID_SURVEY_RESPONSE, "모든 질문에 답변해야 합니다."));

        // when & then
        mockMvc.perform(post("/api/v1/surveys/{surveyId}/submit", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("S003"));
    }

    @Test
    @DisplayName("POST /api/v1/surveys/{surveyId}/submit - 존재하지 않는 선택지 (실패)")
    void submitSurvey_InvalidOption_Fail() throws Exception {
        // given
        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(1L)
                .answers(List.of(
                        new SurveyAnswerDto(1L, 999L) // 존재하지 않는 옵션
                ))
                .build();

        given(surveyResponseService.submitSurvey(any(SubmitSurveyRequest.class)))
                .willThrow(new BusinessException(ErrorCode.INVALID_SURVEY_RESPONSE, "잘못된 선택지입니다."));

        // when & then
        mockMvc.perform(post("/api/v1/surveys/{surveyId}/submit", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("S003"));
    }

    @Test
    @DisplayName("POST /api/v1/surveys/{surveyId}/submit - 존재하지 않는 설문 (실패)")
    void submitSurvey_SurveyNotFound_Fail() throws Exception {
        // given
        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(999L)
                .answers(List.of(new SurveyAnswerDto(1L, 1L)))
                .build();

        given(surveyResponseService.submitSurvey(any(SubmitSurveyRequest.class)))
                .willThrow(new BusinessException(ErrorCode.SURVEY_NOT_FOUND));

        // when & then
        mockMvc.perform(post("/api/v1/surveys/{surveyId}/submit", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("S001"));
    }

}