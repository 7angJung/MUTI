package com.muti.domain.survey.controller;

import com.muti.domain.survey.dto.request.SubmitSurveyRequest;
import com.muti.domain.survey.dto.response.SurveyDto;
import com.muti.domain.survey.dto.response.SurveyResultDto;
import com.muti.domain.survey.service.SurveyResponseService;
import com.muti.domain.survey.service.SurveyService;
import com.muti.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 설문 조사 관련 API
 * MUTI 음악 성향 분석을 위한 설문 관리
 */
@Slf4j
@Tag(name = "Survey", description = "음악 성향 설문 API")
@RestController
@RequestMapping("/api/v1/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;
    private final SurveyResponseService surveyResponseService;

    /**
     * 헬스체크 엔드포인트
     */
    @Operation(summary = "Survey API 헬스체크", description = "Survey 도메인 API가 정상 동작하는지 확인합니다.")
    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.success("pong");
    }

    /**
     * 활성화된 설문 목록 조회
     */
    @Operation(summary = "활성 설문 목록", description = "현재 활성화된 설문 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<SurveyDto>> getActiveSurveys() {
        log.info("GET /api/v1/surveys - Fetching active surveys");
        List<SurveyDto> surveys = surveyService.getActiveSurveys();
        return ApiResponse.success(surveys);
    }

    /**
     * 설문 상세 조회 (질문 및 선택지 포함)
     */
    @Operation(summary = "설문 상세 조회", description = "설문의 질문과 선택지를 포함한 상세 정보를 조회합니다.")
    @GetMapping("/{surveyId}")
    public ApiResponse<SurveyDto> getSurveyDetail(
            @Parameter(description = "설문 ID", required = true)
            @PathVariable Long surveyId) {
        log.info("GET /api/v1/surveys/{} - Fetching survey detail", surveyId);
        SurveyDto survey = surveyService.getSurveyDetail(surveyId);
        return ApiResponse.success(survey);
    }

    /**
     * 설문 응답 제출 및 MUTI 타입 산출
     */
    @Operation(summary = "설문 응답 제출", description = "설문 응답을 제출하고 MUTI 타입 결과를 받습니다.")
    @PostMapping("/{surveyId}/submit")
    public ApiResponse<SurveyResultDto> submitSurvey(
            @Parameter(description = "설문 ID", required = true)
            @PathVariable Long surveyId,
            @Valid @RequestBody SubmitSurveyRequest request) {
        log.info("POST /api/v1/surveys/{}/submit - Submitting survey responses", surveyId);

        // surveyId를 request에 설정 (path variable 우선)
        request = SubmitSurveyRequest.builder()
                .surveyId(surveyId)
                .answers(request.getAnswers())
                .sessionId(request.getSessionId())
                .userId(request.getUserId())
                .build();

        SurveyResultDto result = surveyResponseService.submitSurvey(request);
        return ApiResponse.success(result, "MUTI 타입이 성공적으로 산출되었습니다.");
    }
}