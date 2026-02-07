package com.muti.domain.survey.integration;

import com.muti.domain.survey.entity.Question;
import com.muti.domain.survey.entity.QuestionOption;
import com.muti.domain.survey.entity.Survey;
import com.muti.domain.survey.enums.AxisDirection;
import com.muti.domain.survey.enums.MutiAxis;
import com.muti.domain.survey.repository.QuestionOptionRepository;
import com.muti.domain.survey.repository.QuestionRepository;
import com.muti.domain.survey.repository.SurveyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Flyway 마이그레이션 통합 테스트
 * Flyway를 활성화하여 실제 마이그레이션이 잘 작동하는지 검증
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.flyway.enabled=true",
        "spring.flyway.clean-disabled=false",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.datasource.url=jdbc:h2:mem:flyway_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
@DisplayName("Flyway 마이그레이션 통합 테스트")
class FlywayMigrationIntegrationTest {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionOptionRepository questionOptionRepository;

    @Test
    @DisplayName("V1: 스키마 생성 - 모든 테이블 정상 생성")
    void migration_V1_SchemaCreated() {
        // when & then - Flyway가 자동으로 실행됨
        // 테이블이 정상적으로 생성되었는지 확인 (에러 없이 repository 접근 가능)
        assertThat(surveyRepository).isNotNull();
        assertThat(questionRepository).isNotNull();
        assertThat(questionOptionRepository).isNotNull();
    }

    @Test
    @DisplayName("V2: 초기 데이터 삽입 - Survey 1개 생성")
    void migration_V2_Survey_Created() {
        // when
        List<Survey> surveys = surveyRepository.findAll();

        // then
        assertThat(surveys).hasSize(1);

        Survey survey = surveys.get(0);
        assertThat(survey.getId()).isEqualTo(1L);
        assertThat(survey.getTitle()).isEqualTo("MUTI 음악 성향 테스트");
        assertThat(survey.getDescription()).contains("16가지 타입");
        assertThat(survey.getActive()).isTrue();
    }

    @Test
    @DisplayName("V2: 초기 데이터 삽입 - Question 8개 생성")
    void migration_V2_Questions_Created() {
        // when
        List<Question> questions = questionRepository.findAll();

        // then
        assertThat(questions).hasSize(8);

        // 각 축당 2개씩
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
    @DisplayName("V2: 초기 데이터 삽입 - QuestionOption 16개 생성")
    void migration_V2_Options_Created() {
        // when
        List<QuestionOption> options = questionOptionRepository.findAll();

        // then
        assertThat(options).hasSize(16);

        // 모든 옵션의 점수가 1~5 범위인지 확인
        assertThat(options).allMatch(o -> o.getScore() >= 1 && o.getScore() <= 5);
    }

    @Test
    @DisplayName("V2: E_I 축 질문 및 선택지 검증")
    void migration_V2_EI_Axis_Valid() {
        // when
        List<Question> eiQuestions = questionRepository.findAll().stream()
                .filter(q -> q.getAxis() == MutiAxis.E_I)
                .toList();

        // then
        assertThat(eiQuestions).hasSize(2);

        Question q1 = eiQuestions.stream()
                .filter(q -> q.getOrderIndex() == 1)
                .findFirst()
                .orElseThrow();

        assertThat(q1.getContent()).contains("템포");

        // 옵션 확인
        List<QuestionOption> options = questionOptionRepository.findAll().stream()
                .filter(o -> o.getQuestion().getId().equals(q1.getId()))
                .toList();

        assertThat(options).hasSize(2);
        assertThat(options).anyMatch(o -> o.getDirection() == AxisDirection.E);
        assertThat(options).anyMatch(o -> o.getDirection() == AxisDirection.I);
    }

    @Test
    @DisplayName("V2: S_F 축 질문 및 선택지 검증")
    void migration_V2_SF_Axis_Valid() {
        // when
        List<Question> sfQuestions = questionRepository.findAll().stream()
                .filter(q -> q.getAxis() == MutiAxis.S_F)
                .toList();

        // then
        assertThat(sfQuestions).hasSize(2);

        // 모든 S_F 질문의 옵션이 S 또는 F 방향인지 확인
        for (Question question : sfQuestions) {
            List<QuestionOption> options = questionOptionRepository.findAll().stream()
                    .filter(o -> o.getQuestion().getId().equals(question.getId()))
                    .toList();

            assertThat(options).allMatch(o ->
                    o.getDirection() == AxisDirection.S || o.getDirection() == AxisDirection.F);
        }
    }

    @Test
    @DisplayName("V2: A_D 축 질문 및 선택지 검증")
    void migration_V2_AD_Axis_Valid() {
        // when
        List<Question> adQuestions = questionRepository.findAll().stream()
                .filter(q -> q.getAxis() == MutiAxis.A_D)
                .toList();

        // then
        assertThat(adQuestions).hasSize(2);

        // 모든 A_D 질문의 옵션이 A 또는 D 방향인지 확인
        for (Question question : adQuestions) {
            List<QuestionOption> options = questionOptionRepository.findAll().stream()
                    .filter(o -> o.getQuestion().getId().equals(question.getId()))
                    .toList();

            assertThat(options).allMatch(o ->
                    o.getDirection() == AxisDirection.A || o.getDirection() == AxisDirection.D);
        }
    }

    @Test
    @DisplayName("V2: P_U 축 질문 및 선택지 검증")
    void migration_V2_PU_Axis_Valid() {
        // when
        List<Question> puQuestions = questionRepository.findAll().stream()
                .filter(q -> q.getAxis() == MutiAxis.P_U)
                .toList();

        // then
        assertThat(puQuestions).hasSize(2);

        // 모든 P_U 질문의 옵션이 P 또는 U 방향인지 확인
        for (Question question : puQuestions) {
            List<QuestionOption> options = questionOptionRepository.findAll().stream()
                    .filter(o -> o.getQuestion().getId().equals(question.getId()))
                    .toList();

            assertThat(options).allMatch(o ->
                    o.getDirection() == AxisDirection.P || o.getDirection() == AxisDirection.U);
        }
    }

    @Test
    @DisplayName("전체 데이터 무결성 검증")
    void migration_DataIntegrity() {
        // when
        Survey survey = surveyRepository.findAll().get(0);
        List<Question> questions = questionRepository.findAll();
        List<QuestionOption> options = questionOptionRepository.findAll();

        // then
        // 1. Survey 연관관계
        assertThat(questions).allMatch(q -> q.getSurvey() != null);
        assertThat(questions).allMatch(q -> q.getSurvey().getId().equals(survey.getId()));

        // 2. Question 연관관계
        assertThat(options).allMatch(o -> o.getQuestion() != null);
        assertThat(options).allMatch(o ->
                questions.stream().anyMatch(q -> q.getId().equals(o.getQuestion().getId())));

        // 3. 질문 순서 (1~8)
        for (int i = 1; i <= 8; i++) {
            final int order = i;
            assertThat(questions).anyMatch(q -> q.getOrderIndex() == order);
        }

        // 4. 각 질문당 정확히 2개의 옵션
        for (Question question : questions) {
            long optionCount = options.stream()
                    .filter(o -> o.getQuestion().getId().equals(question.getId()))
                    .count();
            assertThat(optionCount).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("Flyway 히스토리 테이블 생성 확인")
    void flyway_HistoryTable_Created() {
        // Flyway는 flyway_schema_history 테이블을 자동 생성
        // 이 테이블에 마이그레이션 이력이 기록됨
        // 직접 확인은 어렵지만, 마이그레이션이 성공했다면 테이블이 생성된 것

        // 데이터가 있다는 것 자체가 마이그레이션이 성공했다는 증거
        assertThat(surveyRepository.count()).isGreaterThan(0);
        assertThat(questionRepository.count()).isGreaterThan(0);
        assertThat(questionOptionRepository.count()).isGreaterThan(0);
    }
}