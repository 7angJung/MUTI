package com.muti.domain.survey.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 선택지 조회 응답
 */
@Getter
@AllArgsConstructor
@Builder
public class OptionDto {

    private Long id;
    private String content;
    private Integer orderIndex;

    // 점수와 방향은 클라이언트에 노출하지 않음 (보안)
}