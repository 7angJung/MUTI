package com.muti.domain.survey.entity;

import com.muti.domain.survey.enums.MutiType;
import com.muti.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 설문 결과
 * 사용자의 설문 응답을 기반으로 산출된 MUTI 타입
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "survey_results")
public class SurveyResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MutiType mutiType;

    /**
     * E_I 축 점수 (E쪽이 양수, I쪽이 음수)
     */
    @Column(nullable = false)
    private Integer eiScore;

    /**
     * S_F 축 점수 (S쪽이 양수, F쪽이 음수)
     */
    @Column(nullable = false)
    private Integer sfScore;

    /**
     * A_D 축 점수 (A쪽이 양수, D쪽이 음수)
     */
    @Column(nullable = false)
    private Integer adScore;

    /**
     * P_U 축 점수 (P쪽이 양수, U쪽이 음수)
     */
    @Column(nullable = false)
    private Integer puScore;

    @OneToMany(mappedBy = "surveyResult", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SurveyResponse> responses = new ArrayList<>();

    /**
     * 세션 ID (비로그인 사용자 식별용, 향후 확장)
     */
    @Column(length = 100)
    private String sessionId;

    /**
     * 사용자 ID (향후 로그인 기능 추가 시)
     */
    @Column
    private Long userId;

    /**
     * 응답 추가
     */
    public void addResponse(SurveyResponse response) {
        this.responses.add(response);
        response.assignResult(this);
    }
}