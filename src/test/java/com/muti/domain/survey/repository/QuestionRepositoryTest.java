package com.muti.domain.survey.repository;

import com.muti.config.TestJpaConfig;
import com.muti.domain.survey.entity.Question;
import com.muti.domain.survey.entity.QuestionOption;
import com.muti.domain.survey.entity.Survey;
import com.muti.domain.survey.enums.AxisDirection;
import com.muti.domain.survey.enums.MutiAxis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * QuestionRepository 통합 테스트
 */
@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
@DisplayName("QuestionRepository 통합 테스트")
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Test
    @DisplayName("설문 ID로 질문 목록 조회 (순서대로)")
    void findBySurveyIdOrderByOrderIndex() {
        // given
        Survey survey = surveyRepository.save(Survey.builder().title("테스트").build());

        Question q3 = Question.builder().content("질문3").axis(MutiAxis.A_D).orderIndex(3).build();
        Question q1 = Question.builder().content("질문1").axis(MutiAxis.E_I).orderIndex(1).build();
        Question q2 = Question.builder().content("질문2").axis(MutiAxis.S_F).orderIndex(2).build();

        survey.addQuestion(q3);
        survey.addQuestion(q1);
        survey.addQuestion(q2);

        surveyRepository.save(survey);

        // when
        List<Question> results = questionRepository.findBySurveyIdOrderByOrderIndex(survey.getId());

        // then
        assertThat(results).hasSize(3);
        assertThat(results).extracting(Question::getContent)
                .containsExactly("질문1", "질문2", "질문3");
    }

    @Test
    @DisplayName("특정 축의 질문 조회")
    void findByAxis() {
        // given
        Survey survey = surveyRepository.save(Survey.builder().title("테스트").build());

        Question qEI = Question.builder().content("EI질문").axis(MutiAxis.E_I).orderIndex(1).build();
        Question qSF = Question.builder().content("SF질문").axis(MutiAxis.S_F).orderIndex(2).build();
        Question qEI2 = Question.builder().content("EI질문2").axis(MutiAxis.E_I).orderIndex(3).build();

        survey.addQuestion(qEI);
        survey.addQuestion(qSF);
        survey.addQuestion(qEI2);

        surveyRepository.save(survey);

        // when
        List<Question> results = questionRepository.findByAxis(MutiAxis.E_I);

        // then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Question::getContent)
                .containsExactlyInAnyOrder("EI질문", "EI질문2");
    }

    @Test
    @DisplayName("설문의 질문과 선택지 함께 조회")
    void findBySurveyIdWithOptions() {
        // given
        Survey survey = surveyRepository.save(Survey.builder().title("테스트").build());

        Question question = Question.builder()
                .content("질문")
                .axis(MutiAxis.E_I)
                .orderIndex(1)
                .build();

        QuestionOption o1 = QuestionOption.builder()
                .content("선택1").direction(AxisDirection.E).score(5).orderIndex(1).build();
        QuestionOption o2 = QuestionOption.builder()
                .content("선택2").direction(AxisDirection.I).score(-3).orderIndex(2).build();

        question.addOption(o1);
        question.addOption(o2);
        survey.addQuestion(question);

        surveyRepository.save(survey);

        // when
        List<Question> results = questionRepository.findBySurveyIdWithOptions(survey.getId());

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getOptions()).hasSize(2);
    }

    @Test
    @DisplayName("존재하지 않는 설문 ID 조회 - 빈 결과")
    void findBySurveyIdNotFound() {
        // when
        List<Question> results = questionRepository.findBySurveyIdOrderByOrderIndex(99999L);

        // then
        assertThat(results).isEmpty();
    }
}