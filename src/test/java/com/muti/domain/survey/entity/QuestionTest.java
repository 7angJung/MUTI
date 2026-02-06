package com.muti.domain.survey.entity;

import com.muti.domain.survey.enums.AxisDirection;
import com.muti.domain.survey.enums.MutiAxis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Question 엔티티 단위 테스트
 */
@DisplayName("Question 엔티티 테스트")
class QuestionTest {

    @Test
    @DisplayName("Question 생성 - 성공")
    void createQuestion_Success() {
        // when
        Question question = Question.builder()
                .content("음악을 들을 때 가사에 집중하나요?")
                .axis(MutiAxis.E_I)
                .orderIndex(1)
                .build();

        // then
        assertThat(question.getContent()).isEqualTo("음악을 들을 때 가사에 집중하나요?");
        assertThat(question.getAxis()).isEqualTo(MutiAxis.E_I);
        assertThat(question.getOrderIndex()).isEqualTo(1);
        assertThat(question.getOptions()).isEmpty();
    }

    @Test
    @DisplayName("Question에 Survey 할당")
    void assignSurvey_Success() {
        // given
        Survey survey = Survey.builder()
                .title("테스트 설문")
                .build();

        Question question = Question.builder()
                .content("질문")
                .axis(MutiAxis.E_I)
                .orderIndex(1)
                .build();

        // when
        question.assignSurvey(survey);

        // then
        assertThat(question.getSurvey()).isEqualTo(survey);
    }

    @Test
    @DisplayName("Question에 Option 추가 - 성공")
    void addOption_Success() {
        // given
        Question question = Question.builder()
                .content("질문")
                .axis(MutiAxis.E_I)
                .orderIndex(1)
                .build();

        QuestionOption option = QuestionOption.builder()
                .content("매우 그렇다")
                .direction(AxisDirection.E)
                .score(5)
                .orderIndex(1)
                .build();

        // when
        question.addOption(option);

        // then
        assertThat(question.getOptions()).hasSize(1);
        assertThat(question.getOptions().get(0)).isEqualTo(option);
        assertThat(option.getQuestion()).isEqualTo(question);
    }

    @Test
    @DisplayName("Question에 여러 Option 추가")
    void addMultipleOptions() {
        // given
        Question question = Question.builder()
                .content("질문")
                .axis(MutiAxis.E_I)
                .orderIndex(1)
                .build();

        QuestionOption o1 = QuestionOption.builder()
                .content("선택1").direction(AxisDirection.E).score(5).orderIndex(1).build();
        QuestionOption o2 = QuestionOption.builder()
                .content("선택2").direction(AxisDirection.I).score(-3).orderIndex(2).build();

        // when
        question.addOption(o1);
        question.addOption(o2);

        // then
        assertThat(question.getOptions()).hasSize(2);
        assertThat(o1.getQuestion()).isEqualTo(question);
        assertThat(o2.getQuestion()).isEqualTo(question);
    }
}