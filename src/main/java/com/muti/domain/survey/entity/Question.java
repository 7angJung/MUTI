package com.muti.domain.survey.entity;

import com.muti.domain.survey.enums.MutiAxis;
import com.muti.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 질문
 * 특정 MUTI 축을 측정하는 질문
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "questions")
public class Question extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MutiAxis axis;

    @Column(nullable = false)
    private Integer orderIndex;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuestionOption> options = new ArrayList<>();

    /**
     * 설문 할당 (연관관계 편의 메서드)
     */
    public void assignSurvey(Survey survey) {
        this.survey = survey;
    }

    /**
     * 선택지 추가
     */
    public void addOption(QuestionOption option) {
        this.options.add(option);
        option.assignQuestion(this);
    }
}