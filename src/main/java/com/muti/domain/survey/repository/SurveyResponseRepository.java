package com.muti.domain.survey.repository;

import com.muti.domain.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * SurveyResponse Repository
 */
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

    /**
     * 결과 ID로 응답 목록 조회
     */
    List<SurveyResponse> findBySurveyResultId(Long surveyResultId);

    /**
     * 결과 ID로 응답과 질문, 선택지 함께 조회
     */
    @Query("SELECT sr FROM SurveyResponse sr " +
           "JOIN FETCH sr.question q " +
           "JOIN FETCH sr.selectedOption o " +
           "WHERE sr.surveyResult.id = :resultId")
    List<SurveyResponse> findBySurveyResultIdWithDetails(@Param("resultId") Long resultId);
}