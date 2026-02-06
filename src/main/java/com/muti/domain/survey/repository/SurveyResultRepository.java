package com.muti.domain.survey.repository;

import com.muti.domain.survey.entity.SurveyResult;
import com.muti.domain.survey.enums.MutiType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * SurveyResult Repository
 */
public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {

    /**
     * 세션 ID로 결과 조회 (비로그인 사용자)
     */
    List<SurveyResult> findBySessionId(String sessionId);

    /**
     * 사용자 ID로 결과 조회 (향후 로그인 기능)
     */
    List<SurveyResult> findByUserId(Long userId);

    /**
     * MUTI 타입으로 결과 조회
     */
    List<SurveyResult> findByMutiType(MutiType mutiType);

    /**
     * 결과와 응답 함께 조회
     */
    @Query("SELECT sr FROM SurveyResult sr " +
           "LEFT JOIN FETCH sr.responses " +
           "WHERE sr.id = :resultId")
    Optional<SurveyResult> findByIdWithResponses(@Param("resultId") Long resultId);

    /**
     * 설문별 MUTI 타입 분포 통계
     */
    @Query("SELECT sr.mutiType as type, COUNT(sr) as count " +
           "FROM SurveyResult sr " +
           "WHERE sr.survey.id = :surveyId " +
           "GROUP BY sr.mutiType " +
           "ORDER BY COUNT(sr) DESC")
    List<Object[]> countByMutiTypeForSurvey(@Param("surveyId") Long surveyId);
}