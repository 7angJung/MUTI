package com.muti.domain.survey.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 설문 응답 제출 요청
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitSurveyRequest {

    @NotNull(message = "설문 ID는 필수입니다.")
    private Long surveyId;

    @NotEmpty(message = "응답은 최소 1개 이상이어야 합니다.")
    @Valid
    private List<SurveyAnswerDto> answers;

    /**
     * 세션 ID (비로그인 사용자)
     */
    private String sessionId;

    /**
     * 사용자 ID (향후 로그인 기능)
     */
    private Long userId;
}