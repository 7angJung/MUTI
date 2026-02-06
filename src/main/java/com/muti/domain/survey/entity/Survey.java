package com.muti.domain.survey.entity;

import com.muti.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 설문
 * MUTI 타입을 측정하기 위한 설문지
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "surveys")
public class Survey extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    /**
     * 질문 추가
     */
    public void addQuestion(Question question) {
        this.questions.add(question);
        question.assignSurvey(this);
    }

    /**
     * 설문 비활성화
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * 설문 활성화
     */
    public void activate() {
        this.active = true;
    }
}