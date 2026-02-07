package com.muti.domain.survey.integration;

import com.muti.domain.survey.entity.Question;
import com.muti.domain.survey.entity.QuestionOption;
import com.muti.domain.survey.entity.Survey;
import com.muti.domain.survey.enums.AxisDirection;
import com.muti.domain.survey.enums.MutiAxis;
import com.muti.domain.survey.repository.QuestionRepository;
import com.muti.domain.survey.repository.SurveyRepository;
import com.muti.domain.survey.service.SurveyDataInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * 설문 데이터 초기화 통합 테스트
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
@DisplayName("설문 데이터 초기화 테스트")
class SurveyDataInitializerTest {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SurveyDataInitializer surveyDataInitializer;

    @Test
    @DisplayName("초기 설문 데이터 생성 - Survey 1개")
    void initializeData_Survey_Created() throws Exception {
        // given & when
        surveyDataInitializer.run(mock(ApplicationArguments.class));

        // then
        List<Survey> surveys = surveyRepository.findAll();
        assertThat(surveys).hasSize(1);

        Survey survey = surveys.get(0);
        assertThat(survey.getTitle()).isEqualTo("MUTI 음악 성향 테스트");
        assertThat(survey.getDescription()).contains("16가지 타입");
        assertThat(survey.getActive()).isTrue();
    }

    @Test
    @DisplayName("초기 설문 데이터 생성 - Question 8개 (각 축당 2개)")
    void initializeData_Questions_Created() throws Exception {
        // given & when
        surveyDataInitializer.run(mock(ApplicationArguments.class));

        // then
        List<Question> questions = questionRepository.findAll();
        assertThat(questions).hasSize(8);

        // 각 축당 2개씩 있는지 확인
        long eiCount = questions.stream().filter(q -> q.getAxis() == MutiAxis.E_I).count();
        long sfCount = questions.stream().filter(q -> q.getAxis() == MutiAxis.S_F).count();
        long adCount = questions.stream().filter(q -> q.getAxis() == MutiAxis.A_D).count();
        long puCount = questions.stream().filter(q -> q.getAxis() == MutiAxis.P_U).count();

        assertThat(eiCount).isEqualTo(2);
        assertThat(sfCount).isEqualTo(2);
        assertThat(adCount).isEqualTo(2);
        assertThat(puCount).isEqualTo(2);
    }

    @Test
    @DisplayName("초기 설문 데이터 생성 - QuestionOption 16개 (질문당 2개)")
    void initializeData_QuestionOptions_Created() throws Exception {
        // given & when
        surveyDataInitializer.run(mock(ApplicationArguments.class));

        // then
        List<Question> questions = questionRepository.findAll();
        Survey survey = surveyRepository.findAll().get(0);

        int totalOptions = survey.getQuestions().stream()
                .mapToInt(q -> q.getOptions().size())
                .sum();

        assertThat(totalOptions).isEqualTo(16);

        // 각 질문당 2개씩 있는지 확인
        for (Question question : questions) {
            assertThat(question.getOptions()).hasSize(2);
        }
    }

    @Test
    @DisplayName("초기 설문 데이터 생성 - E_I 축 질문 검증")
    void initializeData_EI_Questions_Valid() throws Exception {
        // given & when
        surveyDataInitializer.run(mock(ApplicationArguments.class));

        // then
        List<Question> eiQuestions = questionRepository.findAll().stream()
                .filter(q -> q.getAxis() == MutiAxis.E_I)
                .toList();

        assertThat(eiQuestions).hasSize(2);

        // 첫 번째 질문
        Question q1 = eiQuestions.stream()
                .filter(q -> q.getOrderIndex() == 1)
                .findFirst()
                .orElseThrow();

        assertThat(q1.getContent()).contains("템포");
        assertThat(q1.getOptions()).hasSize(2);
        assertThat(q1.getOptions()).anyMatch(o -> o.getDirection() == AxisDirection.E);
        assertThat(q1.getOptions()).anyMatch(o -> o.getDirection() == AxisDirection.I);
    }

    @Test
    @DisplayName("초기 설문 데이터 생성 - S_F 축 질문 검증")
    void initializeData_SF_Questions_Valid() throws Exception {
        // given & when
        surveyDataInitializer.run(mock(ApplicationArguments.class));

        // then
        List<Question> sfQuestions = questionRepository.findAll().stream()
                .filter(q -> q.getAxis() == MutiAxis.S_F)
                .toList();

        assertThat(sfQuestions).hasSize(2);

        // 질문 내용 확인
        assertThat(sfQuestions).anyMatch(q -> q.getContent().contains("요소"));
        assertThat(sfQuestions).anyMatch(q -> q.getContent().contains("고를 때"));

        // 모든 옵션이 S 또는 F 방향인지 확인
        for (Question question : sfQuestions) {
            for (QuestionOption option : question.getOptions()) {
                assertThat(option.getDirection()).isIn(AxisDirection.S, AxisDirection.F);
            }
        }
    }

    @Test
    @DisplayName("초기 설문 데이터 생성 - A_D 축 질문 검증")
    void initializeData_AD_Questions_Valid() throws Exception {
        // given & when
        surveyDataInitializer.run(mock(ApplicationArguments.class));

        // then
        List<Question> adQuestions = questionRepository.findAll().stream()
                .filter(q -> q.getAxis() == MutiAxis.A_D)
                .toList();

        assertThat(adQuestions).hasSize(2);

        // 질문 내용 확인
        assertThat(adQuestions).anyMatch(q -> q.getContent().contains("사운드"));
        assertThat(adQuestions).anyMatch(q -> q.getContent().contains("라이브"));

        // 모든 옵션이 A 또는 D 방향인지 확인
        for (Question question : adQuestions) {
            for (QuestionOption option : question.getOptions()) {
                assertThat(option.getDirection()).isIn(AxisDirection.A, AxisDirection.D);
            }
        }
    }

    @Test
    @DisplayName("초기 설문 데이터 생성 - P_U 축 질문 검증")
    void initializeData_PU_Questions_Valid() throws Exception {
        // given & when
        surveyDataInitializer.run(mock(ApplicationArguments.class));

        // then
        List<Question> puQuestions = questionRepository.findAll().stream()
                .filter(q -> q.getAxis() == MutiAxis.P_U)
                .toList();

        assertThat(puQuestions).hasSize(2);

        // 질문 내용 확인
        assertThat(puQuestions).anyMatch(q -> q.getContent().contains("발견"));
        assertThat(puQuestions).anyMatch(q -> q.getContent().contains("스타일"));

        // 모든 옵션이 P 또는 U 방향인지 확인
        for (Question question : puQuestions) {
            for (QuestionOption option : question.getOptions()) {
                assertThat(option.getDirection()).isIn(AxisDirection.P, AxisDirection.U);
            }
        }
    }

    @Test
    @DisplayName("초기 설문 데이터 생성 - 점수 범위 검증 (1~5)")
    void initializeData_Scores_ValidRange() throws Exception {
        // given & when
        surveyDataInitializer.run(mock(ApplicationArguments.class));

        // then
        Survey survey = surveyRepository.findAll().get(0);

        for (Question question : survey.getQuestions()) {
            for (QuestionOption option : question.getOptions()) {
                assertThat(option.getScore()).isBetween(1, 5);
            }
        }
    }

    @Test
    @DisplayName("초기 설문 데이터 생성 - 질문 순서 검증 (1~8)")
    void initializeData_QuestionOrder_Valid() throws Exception {
        // given & when
        surveyDataInitializer.run(mock(ApplicationArguments.class));

        // then
        List<Question> questions = questionRepository.findAll();
        assertThat(questions).hasSize(8);

        // 순서가 1부터 8까지 있는지 확인
        for (int i = 1; i <= 8; i++) {
            final int order = i;
            assertThat(questions).anyMatch(q -> q.getOrderIndex() == order);
        }
    }

    @Test
    @DisplayName("중복 실행 방지 - 이미 데이터가 있으면 스킵")
    void initializeData_SkipIfAlreadyExists() throws Exception {
        // given
        surveyDataInitializer.run(mock(ApplicationArguments.class));
        long initialCount = surveyRepository.count();

        // when - 한 번 더 실행
        surveyDataInitializer.run(mock(ApplicationArguments.class));

        // then - 중복 생성되지 않음
        assertThat(surveyRepository.count()).isEqualTo(initialCount);
    }
}