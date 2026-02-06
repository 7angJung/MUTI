package com.muti.domain.survey.entity;

import com.muti.domain.survey.enums.AxisDirection;
import com.muti.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 질문 선택지
 * 각 선택지는 특정 축 방향에 대한 점수를 가짐
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "question_options")
public class QuestionOption extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false, length = 500)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    private AxisDirection direction;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Integer orderIndex;

    /**
     * 질문 할당 (연관관계 편의 메서드)
     */
    public void assignQuestion(Question question) {
        this.question = question;
    }
}