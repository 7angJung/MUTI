package com.muti.domain.survey.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 설문 조회 응답
 */
@Getter
@AllArgsConstructor
@Builder
public class SurveyDto {

    private Long id;
    private String title;
    private String description;
    private Boolean active;
    private List<QuestionDto> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}