package com.muti.domain.survey.entity;

import com.muti.domain.survey.enums.MutiAxis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Survey 엔티티 단위 테스트
 */
@DisplayName("Survey 엔티티 테스트")
class SurveyTest {

    @Test
    @DisplayName("Survey 생성 - 성공")
    void createSurvey_Success() {
        // when
        Survey survey = Survey.builder()
                .title("MUTI 테스트 설문")
                .description("음악 취향 분석")
                .active(true)
                .build();

        // then
        assertThat(survey.getTitle()).isEqualTo("MUTI 테스트 설문");
        assertThat(survey.getDescription()).isEqualTo("음악 취향 분석");
        assertThat(survey.getActive()).isTrue();
        assertThat(survey.getQuestions()).isEmpty();
    }

    @Test
    @DisplayName("Survey에 Question 추가 - 성공")
    void addQuestion_Success() {
        // given
        Survey survey = Survey.builder()
                .title("테스트 설문")
                .build();

        Question question = Question.builder()
                .content("음악을 들을 때 가사에 집중하나요?")
                .axis(MutiAxis.E_I)
                .orderIndex(1)
                .build();

        // when
        survey.addQuestion(question);

        // then
        assertThat(survey.getQuestions()).hasSize(1);
        assertThat(survey.getQuestions().get(0)).isEqualTo(question);
        assertThat(question.getSurvey()).isEqualTo(survey);
    }

    @Test
    @DisplayName("Survey에 여러 Question 추가")
    void addMultipleQuestions() {
        // given
        Survey survey = Survey.builder()
                .title("테스트 설문")
                .build();

        Question q1 = Question.builder().content("질문1").axis(MutiAxis.E_I).orderIndex(1).build();
        Question q2 = Question.builder().content("질문2").axis(MutiAxis.S_F).orderIndex(2).build();
        Question q3 = Question.builder().content("질문3").axis(MutiAxis.A_D).orderIndex(3).build();

        // when
        survey.addQuestion(q1);
        survey.addQuestion(q2);
        survey.addQuestion(q3);

        // then
        assertThat(survey.getQuestions()).hasSize(3);
        assertThat(q1.getSurvey()).isEqualTo(survey);
        assertThat(q2.getSurvey()).isEqualTo(survey);
        assertThat(q3.getSurvey()).isEqualTo(survey);
    }

    @Test
    @DisplayName("Survey 활성화")
    void activate_Success() {
        // given
        Survey survey = Survey.builder()
                .title("테스트 설문")
                .active(false)
                .build();

        // when
        survey.activate();

        // then
        assertThat(survey.getActive()).isTrue();
    }

    @Test
    @DisplayName("Survey 비활성화")
    void deactivate_Success() {
        // given
        Survey survey = Survey.builder()
                .title("테스트 설문")
                .active(true)
                .build();

        // when
        survey.deactivate();

        // then
        assertThat(survey.getActive()).isFalse();
    }

    @Test
    @DisplayName("Survey 기본값 - active는 true")
    void defaultActive() {
        // when
        Survey survey = Survey.builder()
                .title("테스트 설문")
                .build();

        // then
        assertThat(survey.getActive()).isTrue();
    }
}