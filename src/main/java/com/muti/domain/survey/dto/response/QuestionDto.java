package com.muti.domain.survey.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 질문 조회 응답
 */
@Getter
@AllArgsConstructor
@Builder
public class QuestionDto {

    private Long id;
    private String content;
    private String axis;
    private Integer orderIndex;
    private List<OptionDto> options;
}