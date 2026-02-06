package com.muti.domain.survey.repository;

import com.muti.config.TestJpaConfig;
import com.muti.domain.survey.entity.*;
import com.muti.domain.survey.enums.AxisDirection;
import com.muti.domain.survey.enums.MutiAxis;
import com.muti.domain.survey.enums.MutiType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * SurveyResultRepository 통합 테스트
 */
@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
@DisplayName("SurveyResultRepository 통합 테스트")
class SurveyResultRepositoryTest {

    @Autowired
    private SurveyResultRepository surveyResultRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Test
    @DisplayName("SurveyResult 저장 및 조회")
    void saveSurveyResult() {
        // given
        Survey survey = surveyRepository.save(Survey.builder().title("테스트").build());

        SurveyResult result = SurveyResult.builder()
                .survey(survey)
                .mutiType(MutiType.ESAP)
                .eiScore(10)
                .sfScore(5)
                .adScore(8)
                .puScore(3)
                .sessionId("test-session")
                .build();

        // when
        SurveyResult saved = surveyResultRepository.save(result);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMutiType()).isEqualTo(MutiType.ESAP);
        assertThat(saved.getSessionId()).isEqualTo("test-session");
    }

    @Test
    @DisplayName("세션 ID로 결과 조회")
    void findBySessionId() {
        // given
        Survey survey = surveyRepository.save(Survey.builder().title("테스트").build());

        SurveyResult r1 = SurveyResult.builder()
                .survey(survey).mutiType(MutiType.ESAP)
                .eiScore(10).sfScore(5).adScore(8).puScore(3)
                .sessionId("session-123")
                .build();

        SurveyResult r2 = SurveyResult.builder()
                .survey(survey).mutiType(MutiType.IFDU)
                .eiScore(-10).sfScore(-5).adScore(-8).puScore(-3)
                .sessionId("session-123")
                .build();

        surveyResultRepository.saveAll(List.of(r1, r2));

        // when
        List<SurveyResult> results = surveyResultRepository.findBySessionId("session-123");

        // then
        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("MUTI 타입으로 결과 조회")
    void findByMutiType() {
        // given
        Survey survey = surveyRepository.save(Survey.builder().title("테스트").build());

        SurveyResult r1 = SurveyResult.builder()
                .survey(survey).mutiType(MutiType.ESAP)
                .eiScore(10).sfScore(5).adScore(8).puScore(3)
                .build();

        SurveyResult r2 = SurveyResult.builder()
                .survey(survey).mutiType(MutiType.ESAP)
                .eiScore(12).sfScore(7).adScore(6).puScore(4)
                .build();

        SurveyResult r3 = SurveyResult.builder()
                .survey(survey).mutiType(MutiType.IFDU)
                .eiScore(-10).sfScore(-5).adScore(-8).puScore(-3)
                .build();

        surveyResultRepository.saveAll(List.of(r1, r2, r3));

        // when
        List<SurveyResult> results = surveyResultRepository.findByMutiType(MutiType.ESAP);

        // then
        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("결과와 응답 함께 조회")
    void findByIdWithResponses() {
        // given
        Survey survey = Survey.builder().title("테스트").build();
        Question question = Question.builder()
                .content("질문").axis(MutiAxis.E_I).orderIndex(1).build();
        QuestionOption option = QuestionOption.builder()
                .content("선택").direction(AxisDirection.E).score(5).orderIndex(1).build();

        question.addOption(option);
        survey.addQuestion(question);
        Survey savedSurvey = surveyRepository.save(survey);

        // 저장된 엔티티에서 참조 가져오기
        Question savedQuestion = savedSurvey.getQuestions().get(0);
        QuestionOption savedOption = savedQuestion.getOptions().get(0);

        SurveyResult result = SurveyResult.builder()
                .survey(savedSurvey).mutiType(MutiType.ESAP)
                .eiScore(10).sfScore(5).adScore(8).puScore(3)
                .build();

        SurveyResponse response = SurveyResponse.builder()
                .question(savedQuestion)
                .selectedOption(savedOption)
                .build();

        result.addResponse(response);
        SurveyResult saved = surveyResultRepository.save(result);

        // when
        Optional<SurveyResult> found = surveyResultRepository.findByIdWithResponses(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getResponses()).hasSize(1);
    }

    @Test
    @DisplayName("존재하지 않는 세션 ID 조회 - 빈 결과")
    void findBySessionIdNotFound() {
        // when
        List<SurveyResult> results = surveyResultRepository.findBySessionId("non-existent");

        // then
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("userId로 결과 조회")
    void findByUserId() {
        // given
        Survey survey = surveyRepository.save(Survey.builder().title("테스트").build());

        SurveyResult r1 = SurveyResult.builder()
                .survey(survey).mutiType(MutiType.ESAP)
                .eiScore(10).sfScore(5).adScore(8).puScore(3)
                .userId(100L)
                .build();

        surveyResultRepository.save(r1);

        // when
        List<SurveyResult> results = surveyResultRepository.findByUserId(100L);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getUserId()).isEqualTo(100L);
    }
}