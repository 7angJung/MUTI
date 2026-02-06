package com.muti.domain.survey.entity;

import com.muti.domain.survey.enums.AxisDirection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * QuestionOption 엔티티 단위 테스트
 */
@DisplayName("QuestionOption 엔티티 테스트")
class QuestionOptionTest {

    @Test
    @DisplayName("QuestionOption 생성 - 성공")
    void createQuestionOption_Success() {
        // when
        QuestionOption option = QuestionOption.builder()
                .content("매우 그렇다")
                .direction(AxisDirection.E)
                .score(5)
                .orderIndex(1)
                .build();

        // then
        assertThat(option.getContent()).isEqualTo("매우 그렇다");
        assertThat(option.getDirection()).isEqualTo(AxisDirection.E);
        assertThat(option.getScore()).isEqualTo(5);
        assertThat(option.getOrderIndex()).isEqualTo(1);
    }

    @Test
    @DisplayName("음수 점수를 가진 Option 생성")
    void createOptionWithNegativeScore() {
        // when
        QuestionOption option = QuestionOption.builder()
                .content("전혀 그렇지 않다")
                .direction(AxisDirection.I)
                .score(-5)
                .orderIndex(2)
                .build();

        // then
        assertThat(option.getScore()).isEqualTo(-5);
        assertThat(option.getDirection()).isEqualTo(AxisDirection.I);
    }

    @Test
    @DisplayName("QuestionOption에 Question 할당")
    void assignQuestion_Success() {
        // given
        QuestionOption option = QuestionOption.builder()
                .content("선택지")
                .direction(AxisDirection.E)
                .score(3)
                .orderIndex(1)
                .build();

        Question question = Question.builder()
                .content("질문")
                .build();

        // when
        option.assignQuestion(question);

        // then
        assertThat(option.getQuestion()).isEqualTo(question);
    }
}