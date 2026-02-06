package com.muti.domain.survey.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 설문 결과 응답
 */
@Getter
@AllArgsConstructor
@Builder
public class SurveyResultDto {

    private Long id;
    private Long surveyId;

    /**
     * MUTI 타입 코드 (예: ESAP, IFDU)
     */
    private String mutiType;

    /**
     * MUTI 타입 이름
     */
    private String mutiTypeName;

    /**
     * MUTI 타입 설명
     */
    private String mutiTypeDescription;

    /**
     * 각 축별 점수
     * key: E_I, S_F, A_D, P_U
     * value: 점수
     */
    private Map<String, Integer> axisScores;

    /**
     * 각 축별 결정된 방향
     * key: E_I, S_F, A_D, P_U
     * value: E/I, S/F, A/D, P/U
     */
    private Map<String, String> axisDirections;

    /**
     * 결과 생성 시각
     */
    private LocalDateTime createdAt;
}