package com.muti.domain.survey.controller;

import com.muti.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 설문 조사 관련 API
 * MUTI 음악 성향 분석을 위한 설문 관리
 */
@Tag(name = "Survey", description = "음악 성향 설문 API")
@RestController
@RequestMapping("/api/v1/surveys")
public class SurveyController {

    /**
     * 헬스체크 엔드포인트
     * API 동작 확인용
     */
    @Operation(summary = "Survey API 헬스체크", description = "Survey 도메인 API가 정상 동작하는지 확인합니다.")
    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.success("pong");
    }
}