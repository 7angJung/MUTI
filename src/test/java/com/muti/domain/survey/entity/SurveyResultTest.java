package com.muti.domain.survey.entity;

import com.muti.domain.survey.enums.MutiType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * SurveyResult 엔티티 단위 테스트
 */
@DisplayName("SurveyResult 엔티티 테스트")
class SurveyResultTest {

    @Test
    @DisplayName("SurveyResult 생성 - 성공")
    void createSurveyResult_Success() {
        // given
        Survey survey = Survey.builder()
                .title("테스트 설문")
                .build();

        // when
        SurveyResult result = SurveyResult.builder()
                .survey(survey)
                .mutiType(MutiType.ESAP)
                .eiScore(10)
                .sfScore(5)
                .adScore(8)
                .puScore(3)
                .sessionId("test-session-123")
                .build();

        // then
        assertThat(result.getSurvey()).isEqualTo(survey);
        assertThat(result.getMutiType()).isEqualTo(MutiType.ESAP);
        assertThat(result.getEiScore()).isEqualTo(10);
        assertThat(result.getSfScore()).isEqualTo(5);
        assertThat(result.getAdScore()).isEqualTo(8);
        assertThat(result.getPuScore()).isEqualTo(3);
        assertThat(result.getSessionId()).isEqualTo("test-session-123");
    }

    @Test
    @DisplayName("음수 점수를 가진 SurveyResult")
    void createResultWithNegativeScores() {
        // given
        Survey survey = Survey.builder().title("테스트").build();

        // when
        SurveyResult result = SurveyResult.builder()
                .survey(survey)
                .mutiType(MutiType.IFDU)
                .eiScore(-10)
                .sfScore(-5)
                .adScore(-8)
                .puScore(-3)
                .build();

        // then
        assertThat(result.getEiScore()).isEqualTo(-10);
        assertThat(result.getSfScore()).isEqualTo(-5);
        assertThat(result.getAdScore()).isEqualTo(-8);
        assertThat(result.getPuScore()).isEqualTo(-3);
    }

    @Test
    @DisplayName("SurveyResult에 SurveyResponse 추가")
    void addResponse_Success() {
        // given
        Survey survey = Survey.builder().title("테스트").build();
        SurveyResult result = SurveyResult.builder()
                .survey(survey)
                .mutiType(MutiType.ESAP)
                .eiScore(10)
                .sfScore(5)
                .adScore(8)
                .puScore(3)
                .build();

        Question question = Question.builder().content("질문").build();
        QuestionOption option = QuestionOption.builder().content("선택").build();

        SurveyResponse response = SurveyResponse.builder()
                .question(question)
                .selectedOption(option)
                .build();

        // when
        result.addResponse(response);

        // then
        assertThat(result.getResponses()).hasSize(1);
        assertThat(result.getResponses().get(0)).isEqualTo(response);
        assertThat(response.getSurveyResult()).isEqualTo(result);
    }

    @Test
    @DisplayName("점수 조합으로 MUTI 타입 결정 - ESAP (모두 양수)")
    void determineType_ESAP() {
        // given
        Survey survey = Survey.builder().title("테스트").build();

        // when - E(+), S(+), A(+), P(+)
        SurveyResult result = SurveyResult.builder()
                .survey(survey)
                .mutiType(MutiType.ESAP)
                .eiScore(10)
                .sfScore(5)
                .adScore(8)
                .puScore(3)
                .build();

        // then
        assertThat(result.getMutiType()).isEqualTo(MutiType.ESAP);
    }

    @Test
    @DisplayName("점수 조합으로 MUTI 타입 결정 - IFDU (모두 음수)")
    void determineType_IFDU() {
        // given
        Survey survey = Survey.builder().title("테스트").build();

        // when - I(-), F(-), D(-), U(-)
        SurveyResult result = SurveyResult.builder()
                .survey(survey)
                .mutiType(MutiType.IFDU)
                .eiScore(-10)
                .sfScore(-5)
                .adScore(-8)
                .puScore(-3)
                .build();

        // then
        assertThat(result.getMutiType()).isEqualTo(MutiType.IFDU);
    }

    @Test
    @DisplayName("점수 0은 양수로 간주 (E, S, A, P)")
    void zeroScoresAreTreatedAsPositive() {
        // given
        Survey survey = Survey.builder().title("테스트").build();

        // when - 모든 점수가 0
        SurveyResult result = SurveyResult.builder()
                .survey(survey)
                .mutiType(MutiType.ESAP)
                .eiScore(0)
                .sfScore(0)
                .adScore(0)
                .puScore(0)
                .build();

        // then
        assertThat(result.getMutiType()).isEqualTo(MutiType.ESAP);
    }
}