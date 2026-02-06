package com.muti.domain.survey.mapper;

import com.muti.domain.survey.dto.response.QuestionDto;
import com.muti.domain.survey.dto.response.SurveyDto;
import com.muti.domain.survey.dto.response.SurveyResultDto;
import com.muti.domain.survey.entity.*;
import com.muti.domain.survey.enums.AxisDirection;
import com.muti.domain.survey.enums.MutiAxis;
import com.muti.domain.survey.enums.MutiType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * SurveyMapper 단위 테스트
 */
@DisplayName("SurveyMapper 테스트")
class SurveyMapperTest {

    private SurveyMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SurveyMapper();
    }

    @Test
    @DisplayName("Survey 엔티티를 SurveyDto로 변환")
    void toResponse_Survey() throws Exception {
        // given
        Survey survey = createSurveyWithTimestamps("MUTI 설문", "음악 취향 분석", true);

        Question q1 = createQuestionWithTimestamps("질문1", MutiAxis.E_I, 1);
        Question q2 = createQuestionWithTimestamps("질문2", MutiAxis.S_F, 2);

        QuestionOption o1 = createOptionWithTimestamps("선택1", AxisDirection.E, 5, 1);
        QuestionOption o2 = createOptionWithTimestamps("선택2", AxisDirection.I, -3, 2);

        q1.addOption(o1);
        q1.addOption(o2);
        survey.addQuestion(q1);
        survey.addQuestion(q2);

        // when
        SurveyDto result = mapper.toResponse(survey);

        // then
        assertThat(result.getTitle()).isEqualTo("MUTI 설문");
        assertThat(result.getDescription()).isEqualTo("음악 취향 분석");
        assertThat(result.getActive()).isTrue();
        assertThat(result.getQuestions()).hasSize(2);
        assertThat(result.getQuestions().get(0).getContent()).isEqualTo("질문1");
        assertThat(result.getQuestions().get(0).getOptions()).hasSize(2);
    }

    @Test
    @DisplayName("Question 순서대로 정렬되어 변환")
    void toResponse_QuestionsOrdered() throws Exception {
        // given
        Survey survey = createSurveyWithTimestamps("테스트", "", true);

        Question q3 = createQuestionWithTimestamps("질문3", MutiAxis.A_D, 3);
        Question q1 = createQuestionWithTimestamps("질문1", MutiAxis.E_I, 1);
        Question q2 = createQuestionWithTimestamps("질문2", MutiAxis.S_F, 2);

        survey.addQuestion(q3);
        survey.addQuestion(q1);
        survey.addQuestion(q2);

        // when
        SurveyDto result = mapper.toResponse(survey);

        // then
        assertThat(result.getQuestions()).extracting(QuestionDto::getContent)
                .containsExactly("질문1", "질문2", "질문3");
    }

    @Test
    @DisplayName("SurveyResult를 SurveyResultDto로 변환")
    void toResultResponse() throws Exception {
        // given
        Survey survey = createSurveyWithTimestamps("테스트", "", true);

        SurveyResult result = createSurveyResultWithTimestamps(
                survey, MutiType.ESAP,
                10, 5, 8, 3
        );

        // when
        SurveyResultDto dto = mapper.toResultResponse(result);

        // then
        assertThat(dto.getMutiType()).isEqualTo("ESAP");
        assertThat(dto.getMutiTypeName()).isEqualTo("감성적 잔잔한 어쿠스틱 대중");
        assertThat(dto.getAxisScores()).containsEntry("E_I", 10);
        assertThat(dto.getAxisScores()).containsEntry("S_F", 5);
        assertThat(dto.getAxisScores()).containsEntry("A_D", 8);
        assertThat(dto.getAxisScores()).containsEntry("P_U", 3);
    }

    @Test
    @DisplayName("양수 점수는 첫 번째 방향으로 결정")
    void toResultResponse_PositiveScores() throws Exception {
        // given
        Survey survey = createSurveyWithTimestamps("테스트", "", true);

        SurveyResult result = createSurveyResultWithTimestamps(
                survey, MutiType.ESAP,
                10, 5, 8, 3
        );

        // when
        SurveyResultDto dto = mapper.toResultResponse(result);

        // then
        assertThat(dto.getAxisDirections()).containsEntry("E_I", "E");
        assertThat(dto.getAxisDirections()).containsEntry("S_F", "S");
        assertThat(dto.getAxisDirections()).containsEntry("A_D", "A");
        assertThat(dto.getAxisDirections()).containsEntry("P_U", "P");
    }

    @Test
    @DisplayName("음수 점수는 두 번째 방향으로 결정")
    void toResultResponse_NegativeScores() throws Exception {
        // given
        Survey survey = createSurveyWithTimestamps("테스트", "", true);

        SurveyResult result = createSurveyResultWithTimestamps(
                survey, MutiType.IFDU,
                -10, -5, -8, -3
        );

        // when
        SurveyResultDto dto = mapper.toResultResponse(result);

        // then
        assertThat(dto.getAxisDirections()).containsEntry("E_I", "I");
        assertThat(dto.getAxisDirections()).containsEntry("S_F", "F");
        assertThat(dto.getAxisDirections()).containsEntry("A_D", "D");
        assertThat(dto.getAxisDirections()).containsEntry("P_U", "U");
    }

    @Test
    @DisplayName("0 점수는 첫 번째 방향으로 결정 (양수 취급)")
    void toResultResponse_ZeroScores() throws Exception {
        // given
        Survey survey = createSurveyWithTimestamps("테스트", "", true);

        SurveyResult result = createSurveyResultWithTimestamps(
                survey, MutiType.ESAP,
                0, 0, 0, 0
        );

        // when
        SurveyResultDto dto = mapper.toResultResponse(result);

        // then
        assertThat(dto.getAxisDirections()).containsEntry("E_I", "E");
        assertThat(dto.getAxisDirections()).containsEntry("S_F", "S");
        assertThat(dto.getAxisDirections()).containsEntry("A_D", "A");
        assertThat(dto.getAxisDirections()).containsEntry("P_U", "P");
    }

    // Helper methods for creating entities with timestamps
    private Survey createSurveyWithTimestamps(String title, String description, Boolean active) throws Exception {
        Survey survey = Survey.builder()
                .title(title)
                .description(description)
                .active(active)
                .build();
        setTimestamps(survey);
        return survey;
    }

    private Question createQuestionWithTimestamps(String content, MutiAxis axis, Integer orderIndex) throws Exception {
        Question question = Question.builder()
                .content(content)
                .axis(axis)
                .orderIndex(orderIndex)
                .build();
        setTimestamps(question);
        return question;
    }

    private QuestionOption createOptionWithTimestamps(String content, AxisDirection direction, Integer score, Integer orderIndex) throws Exception {
        QuestionOption option = QuestionOption.builder()
                .content(content)
                .direction(direction)
                .score(score)
                .orderIndex(orderIndex)
                .build();
        setTimestamps(option);
        return option;
    }

    private SurveyResult createSurveyResultWithTimestamps(Survey survey, MutiType mutiType, Integer ei, Integer sf, Integer ad, Integer pu) throws Exception {
        SurveyResult result = SurveyResult.builder()
                .survey(survey)
                .mutiType(mutiType)
                .eiScore(ei)
                .sfScore(sf)
                .adScore(ad)
                .puScore(pu)
                .build();
        setTimestamps(result);
        return result;
    }

    private void setTimestamps(Object entity) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Field createdAtField = entity.getClass().getSuperclass().getDeclaredField("createdAt");
        Field updatedAtField = entity.getClass().getSuperclass().getDeclaredField("updatedAt");

        createdAtField.setAccessible(true);
        updatedAtField.setAccessible(true);

        createdAtField.set(entity, now);
        updatedAtField.set(entity, now);
    }
}