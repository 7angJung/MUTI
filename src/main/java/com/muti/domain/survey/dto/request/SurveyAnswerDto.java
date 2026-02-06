package com.muti.domain.survey.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 설문 응답 개별 항목
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyAnswerDto {

    @NotNull(message = "질문 ID는 필수입니다.")
    private Long questionId;

    @NotNull(message = "선택지 ID는 필수입니다.")
    private Long optionId;
}