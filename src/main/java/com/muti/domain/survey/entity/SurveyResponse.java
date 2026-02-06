package com.muti.domain.survey.entity;

import com.muti.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 설문 응답
 * 사용자의 질문별 선택 기록
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "survey_responses")
public class SurveyResponse extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_result_id", nullable = false)
    private SurveyResult surveyResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_option_id", nullable = false)
    private QuestionOption selectedOption;

    /**
     * 결과 할당 (연관관계 편의 메서드)
     */
    public void assignResult(SurveyResult result) {
        this.surveyResult = result;
    }
}