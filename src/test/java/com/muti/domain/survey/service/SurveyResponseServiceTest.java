package com.muti.domain.survey.service;

import com.muti.domain.survey.dto.request.SubmitSurveyRequest;
import com.muti.domain.survey.dto.request.SurveyAnswerDto;
import com.muti.domain.survey.dto.response.SurveyResultDto;
import com.muti.domain.survey.entity.*;
import com.muti.domain.survey.enums.AxisDirection;
import com.muti.domain.survey.enums.MutiAxis;
import com.muti.domain.survey.enums.MutiType;
import com.muti.domain.survey.mapper.SurveyMapper;
import com.muti.domain.survey.repository.QuestionOptionRepository;
import com.muti.domain.survey.repository.QuestionRepository;
import com.muti.domain.survey.repository.SurveyResultRepository;
import com.muti.global.error.BusinessException;
import com.muti.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * SurveyResponseService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SurveyResponseService 테스트")
class SurveyResponseServiceTest {

    @Mock
    private SurveyService surveyService;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionOptionRepository questionOptionRepository;

    @Mock
    private SurveyResultRepository surveyResultRepository;

    @Mock
    private SurveyMapper surveyMapper;

    @InjectMocks
    private SurveyResponseService surveyResponseService;

    @Test
    @DisplayName("설문 응답 제출 - ESAP 타입 (모두 양수) 성공")
    void submitSurvey_ESAP_Success() {
        // given
        Survey survey = Survey.builder().title("테스트").active(true).build();

        // E_I 질문 (E 방향 선택 -> 양수)
        Question q1 = createQuestion(1L, "질문1", MutiAxis.E_I);
        QuestionOption o1 = createOption(1L, "E선택", AxisDirection.E, 5);
        q1.addOption(o1);

        // S_F 질문 (S 방향 선택 -> 양수)
        Question q2 = createQuestion(2L, "질문2", MutiAxis.S_F);
        QuestionOption o2 = createOption(2L, "S선택", AxisDirection.S, 3);
        q2.addOption(o2);

        // A_D 질문 (A 방향 선택 -> 양수)
        Question q3 = createQuestion(3L, "질문3", MutiAxis.A_D);
        QuestionOption o3 = createOption(3L, "A선택", AxisDirection.A, 4);
        q3.addOption(o3);

        // P_U 질문 (P 방향 선택 -> 양수)
        Question q4 = createQuestion(4L, "질문4", MutiAxis.P_U);
        QuestionOption o4 = createOption(4L, "P선택", AxisDirection.P, 2);
        q4.addOption(o4);

        List<Question> questions = List.of(q1, q2, q3, q4);

        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(1L)
                .answers(List.of(
                        new SurveyAnswerDto(1L, 1L),
                        new SurveyAnswerDto(2L, 2L),
                        new SurveyAnswerDto(3L, 3L),
                        new SurveyAnswerDto(4L, 4L)
                ))
                .sessionId("test-session")
                .build();

        given(surveyService.getSurveyEntity(1L)).willReturn(survey);
        given(questionRepository.findBySurveyIdWithOptions(1L)).willReturn(questions);
        given(questionOptionRepository.findById(1L)).willReturn(Optional.of(o1));
        given(questionOptionRepository.findById(2L)).willReturn(Optional.of(o2));
        given(questionOptionRepository.findById(3L)).willReturn(Optional.of(o3));
        given(questionOptionRepository.findById(4L)).willReturn(Optional.of(o4));

        SurveyResult savedResult = SurveyResult.builder()
                .survey(survey)
                .mutiType(MutiType.ESAP)
                .eiScore(5)
                .sfScore(3)
                .adScore(4)
                .puScore(2)
                .build();

        given(surveyResultRepository.save(any(SurveyResult.class))).willReturn(savedResult);
        given(surveyMapper.toResultResponse(any(SurveyResult.class)))
                .willReturn(SurveyResultDto.builder().mutiType("ESAP").build());

        // when
        SurveyResultDto result = surveyResponseService.submitSurvey(request);

        // then
        assertThat(result.getMutiType()).isEqualTo("ESAP");

        ArgumentCaptor<SurveyResult> captor = ArgumentCaptor.forClass(SurveyResult.class);
        verify(surveyResultRepository).save(captor.capture());

        SurveyResult captured = captor.getValue();
        assertThat(captured.getMutiType()).isEqualTo(MutiType.ESAP);
        assertThat(captured.getEiScore()).isEqualTo(5);
        assertThat(captured.getSfScore()).isEqualTo(3);
        assertThat(captured.getAdScore()).isEqualTo(4);
        assertThat(captured.getPuScore()).isEqualTo(2);
    }

    @Test
    @DisplayName("설문 응답 제출 - IFDU 타입 (모두 음수) 성공")
    void submitSurvey_IFDU_Success() {
        // given
        Survey survey = Survey.builder().title("테스트").active(true).build();

        Question q1 = createQuestion(1L, "질문1", MutiAxis.E_I);
        QuestionOption o1 = createOption(1L, "I선택", AxisDirection.I, 5);
        q1.addOption(o1);

        Question q2 = createQuestion(2L, "질문2", MutiAxis.S_F);
        QuestionOption o2 = createOption(2L, "F선택", AxisDirection.F, 3);
        q2.addOption(o2);

        Question q3 = createQuestion(3L, "질문3", MutiAxis.A_D);
        QuestionOption o3 = createOption(3L, "D선택", AxisDirection.D, 4);
        q3.addOption(o3);

        Question q4 = createQuestion(4L, "질문4", MutiAxis.P_U);
        QuestionOption o4 = createOption(4L, "U선택", AxisDirection.U, 2);
        q4.addOption(o4);

        List<Question> questions = List.of(q1, q2, q3, q4);

        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(1L)
                .answers(List.of(
                        new SurveyAnswerDto(1L, 1L),
                        new SurveyAnswerDto(2L, 2L),
                        new SurveyAnswerDto(3L, 3L),
                        new SurveyAnswerDto(4L, 4L)
                ))
                .build();

        given(surveyService.getSurveyEntity(1L)).willReturn(survey);
        given(questionRepository.findBySurveyIdWithOptions(1L)).willReturn(questions);
        given(questionOptionRepository.findById(anyLong())).willAnswer(inv -> {
            Long id = inv.getArgument(0);
            if (id == 1L) return Optional.of(o1);
            if (id == 2L) return Optional.of(o2);
            if (id == 3L) return Optional.of(o3);
            if (id == 4L) return Optional.of(o4);
            return Optional.empty();
        });

        SurveyResult savedResult = SurveyResult.builder()
                .survey(survey)
                .mutiType(MutiType.IFDU)
                .eiScore(-5)
                .sfScore(-3)
                .adScore(-4)
                .puScore(-2)
                .build();

        given(surveyResultRepository.save(any())).willReturn(savedResult);
        given(surveyMapper.toResultResponse(any()))
                .willReturn(SurveyResultDto.builder().mutiType("IFDU").build());

        // when
        SurveyResultDto result = surveyResponseService.submitSurvey(request);

        // then
        assertThat(result.getMutiType()).isEqualTo("IFDU");

        ArgumentCaptor<SurveyResult> captor = ArgumentCaptor.forClass(SurveyResult.class);
        verify(surveyResultRepository).save(captor.capture());

        SurveyResult captured = captor.getValue();
        assertThat(captured.getMutiType()).isEqualTo(MutiType.IFDU);
        assertThat(captured.getEiScore()).isNegative();
        assertThat(captured.getSfScore()).isNegative();
        assertThat(captured.getAdScore()).isNegative();
        assertThat(captured.getPuScore()).isNegative();
    }

    @Test
    @DisplayName("비활성화된 설문 제출 - 실패")
    void submitSurvey_InactiveSurvey_Fail() {
        // given
        Survey survey = Survey.builder().title("테스트").active(false).build();

        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(1L)
                .answers(List.of(new SurveyAnswerDto(1L, 1L)))
                .build();

        given(surveyService.getSurveyEntity(1L)).willReturn(survey);

        // when & then
        assertThatThrownBy(() -> surveyResponseService.submitSurvey(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("비활성화된 설문입니다");
    }

    @Test
    @DisplayName("응답 없이 제출 - 실패")
    void submitSurvey_NoAnswers_Fail() {
        // given
        Survey survey = Survey.builder().title("테스트").active(true).build();
        Question q1 = createQuestion(1L, "질문1", MutiAxis.E_I);

        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(1L)
                .answers(List.of())
                .build();

        given(surveyService.getSurveyEntity(1L)).willReturn(survey);
        given(questionRepository.findBySurveyIdWithOptions(1L)).willReturn(List.of(q1));

        // when & then
        assertThatThrownBy(() -> surveyResponseService.submitSurvey(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("응답이 없습니다");
    }

    @Test
    @DisplayName("응답 수가 질문 수와 불일치 - 실패")
    void submitSurvey_AnswerCountMismatch_Fail() {
        // given
        Survey survey = Survey.builder().title("테스트").active(true).build();
        Question q1 = createQuestion(1L, "질문1", MutiAxis.E_I);
        Question q2 = createQuestion(2L, "질문2", MutiAxis.S_F);

        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(1L)
                .answers(List.of(new SurveyAnswerDto(1L, 1L))) // 1개만
                .build();

        given(surveyService.getSurveyEntity(1L)).willReturn(survey);
        given(questionRepository.findBySurveyIdWithOptions(1L)).willReturn(List.of(q1, q2)); // 2개 질문

        // when & then
        assertThatThrownBy(() -> surveyResponseService.submitSurvey(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("모든 질문에 답변해야 합니다");
    }

    @Test
    @DisplayName("존재하지 않는 선택지 제출 - 실패")
    void submitSurvey_InvalidOption_Fail() {
        // given
        Survey survey = Survey.builder().title("테스트").active(true).build();
        Question q1 = createQuestion(1L, "질문1", MutiAxis.E_I);
        QuestionOption o1 = createOption(1L, "선택1", AxisDirection.E, 5);
        q1.addOption(o1);

        SubmitSurveyRequest request = SubmitSurveyRequest.builder()
                .surveyId(1L)
                .answers(List.of(new SurveyAnswerDto(1L, 999L))) // 존재하지 않는 옵션
                .build();

        given(surveyService.getSurveyEntity(1L)).willReturn(survey);
        given(questionRepository.findBySurveyIdWithOptions(1L)).willReturn(List.of(q1));
        given(questionOptionRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> surveyResponseService.submitSurvey(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("잘못된 선택지입니다");
    }

    // Helper methods
    private Question createQuestion(Long id, String content, MutiAxis axis) {
        Question question = Question.builder()
                .content(content)
                .axis(axis)
                .orderIndex(1)
                .build();
        setId(question, id);
        return question;
    }

    private QuestionOption createOption(Long id, String content, AxisDirection direction, int score) {
        QuestionOption option = QuestionOption.builder()
                .content(content)
                .direction(direction)
                .score(score)
                .orderIndex(1)
                .build();
        setId(option, id);
        return option;
    }

    private void setId(Object entity, Long id) {
        try {
            java.lang.reflect.Field field = entity.getClass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}