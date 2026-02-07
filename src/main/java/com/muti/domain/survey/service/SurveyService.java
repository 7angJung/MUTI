package com.muti.domain.survey.service;

import com.muti.domain.survey.dto.response.SurveyDto;
import com.muti.domain.survey.entity.Survey;
import com.muti.domain.survey.mapper.SurveyMapper;
import com.muti.domain.survey.repository.SurveyRepository;
import com.muti.global.error.BusinessException;
import com.muti.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 설문 조회 및 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final SurveyMapper surveyMapper;

    /**
     * 활성화된 설문 목록 조회
     */
    public List<SurveyDto> getActiveSurveys() {
        log.debug("Fetching all active surveys");

        List<Survey> surveys = surveyRepository.findByActiveTrue();

        return surveys.stream()
                .map(surveyMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 설문 상세 조회 (질문 및 선택지 포함)
     */
    public SurveyDto getSurveyDetail(Long surveyId) {
        log.debug("Fetching survey detail: surveyId={}", surveyId);

        Survey survey = surveyRepository.findByIdWithQuestions(surveyId)
                .orElseThrow(() -> {
                    log.warn("Survey not found: surveyId={}", surveyId);
                    return new BusinessException(ErrorCode.SURVEY_NOT_FOUND);
                });

        // 선택지도 함께 로드 (별도 쿼리)
        survey.getQuestions().forEach(question -> {
            question.getOptions().size(); // lazy loading 강제
        });

        return surveyMapper.toResponse(survey);
    }

    /**
     * 설문 ID로 존재 여부 확인
     */
    public boolean existsSurvey(Long surveyId) {
        return surveyRepository.existsById(surveyId);
    }

    /**
     * 설문 조회 (엔티티 반환, 내부 사용)
     */
    public Survey getSurveyEntity(Long surveyId) {
        return surveyRepository.findById(surveyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SURVEY_NOT_FOUND));
    }
}