package com.muti.domain.survey.service;

import com.muti.domain.survey.dto.response.SurveyDto;
import com.muti.domain.survey.entity.Question;
import com.muti.domain.survey.entity.Survey;
import com.muti.domain.survey.enums.MutiAxis;
import com.muti.domain.survey.mapper.SurveyMapper;
import com.muti.domain.survey.repository.SurveyRepository;
import com.muti.global.error.BusinessException;
import com.muti.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * SurveyService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SurveyService 테스트")
class SurveyServiceTest {

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private SurveyMapper surveyMapper;

    @InjectMocks
    private SurveyService surveyService;

    @Test
    @DisplayName("활성화된 설문 목록 조회 - 성공")
    void getActiveSurveys_Success() {
        // given
        Survey survey1 = Survey.builder().title("설문1").active(true).build();
        Survey survey2 = Survey.builder().title("설문2").active(true).build();

        given(surveyRepository.findByActiveTrue()).willReturn(List.of(survey1, survey2));
        given(surveyMapper.toResponse(any(Survey.class)))
                .willReturn(SurveyDto.builder().title("설문1").build())
                .willReturn(SurveyDto.builder().title("설문2").build());

        // when
        List<SurveyDto> results = surveyService.getActiveSurveys();

        // then
        assertThat(results).hasSize(2);
        verify(surveyRepository).findByActiveTrue();
        verify(surveyMapper, times(2)).toResponse(any(Survey.class));
    }

    @Test
    @DisplayName("활성화된 설문이 없을 때 - 빈 리스트 반환")
    void getActiveSurveys_Empty() {
        // given
        given(surveyRepository.findByActiveTrue()).willReturn(List.of());

        // when
        List<SurveyDto> results = surveyService.getActiveSurveys();

        // then
        assertThat(results).isEmpty();
        verify(surveyRepository).findByActiveTrue();
        verify(surveyMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("설문 상세 조회 - 성공")
    void getSurveyDetail_Success() {
        // given
        Long surveyId = 1L;
        Survey survey = Survey.builder().title("테스트 설문").build();
        Question question = Question.builder()
                .content("질문1")
                .axis(MutiAxis.E_I)
                .orderIndex(1)
                .build();
        survey.addQuestion(question);

        given(surveyRepository.findByIdWithQuestions(surveyId))
                .willReturn(Optional.of(survey));
        given(surveyMapper.toResponse(survey))
                .willReturn(SurveyDto.builder().title("테스트 설문").build());

        // when
        SurveyDto result = surveyService.getSurveyDetail(surveyId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("테스트 설문");
        verify(surveyRepository).findByIdWithQuestions(surveyId);
        verify(surveyMapper).toResponse(survey);
    }

    @Test
    @DisplayName("설문 상세 조회 - 존재하지 않는 설문 (실패)")
    void getSurveyDetail_NotFound() {
        // given
        Long surveyId = 999L;
        given(surveyRepository.findByIdWithQuestions(surveyId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> surveyService.getSurveyDetail(surveyId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SURVEY_NOT_FOUND);

        verify(surveyRepository).findByIdWithQuestions(surveyId);
        verify(surveyMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("설문 존재 여부 확인 - 존재함")
    void existsSurvey_True() {
        // given
        Long surveyId = 1L;
        given(surveyRepository.existsById(surveyId)).willReturn(true);

        // when
        boolean result = surveyService.existsSurvey(surveyId);

        // then
        assertThat(result).isTrue();
        verify(surveyRepository).existsById(surveyId);
    }

    @Test
    @DisplayName("설문 존재 여부 확인 - 존재하지 않음")
    void existsSurvey_False() {
        // given
        Long surveyId = 999L;
        given(surveyRepository.existsById(surveyId)).willReturn(false);

        // when
        boolean result = surveyService.existsSurvey(surveyId);

        // then
        assertThat(result).isFalse();
        verify(surveyRepository).existsById(surveyId);
    }

    @Test
    @DisplayName("설문 엔티티 조회 - 성공")
    void getSurveyEntity_Success() {
        // given
        Long surveyId = 1L;
        Survey survey = Survey.builder().title("테스트").build();
        given(surveyRepository.findById(surveyId)).willReturn(Optional.of(survey));

        // when
        Survey result = surveyService.getSurveyEntity(surveyId);

        // then
        assertThat(result).isEqualTo(survey);
        verify(surveyRepository).findById(surveyId);
    }

    @Test
    @DisplayName("설문 엔티티 조회 - 존재하지 않음 (실패)")
    void getSurveyEntity_NotFound() {
        // given
        Long surveyId = 999L;
        given(surveyRepository.findById(surveyId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> surveyService.getSurveyEntity(surveyId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SURVEY_NOT_FOUND);

        verify(surveyRepository).findById(surveyId);
    }
}