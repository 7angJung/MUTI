package com.muti.domain.survey.repository;

import com.muti.domain.survey.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Survey Repository
 */
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    /**
     * 활성화된 설문 목록 조회
     */
    List<Survey> findByActiveTrue();

    /**
     * 설문과 질문을 함께 조회 (N+1 방지)
     */
    @Query("SELECT s FROM Survey s " +
           "LEFT JOIN FETCH s.questions q " +
           "WHERE s.id = :surveyId")
    Optional<Survey> findByIdWithQuestions(@Param("surveyId") Long surveyId);

    /**
     * 설문과 질문, 선택지를 모두 조회 (N+1 방지)
     */
    @Query("SELECT DISTINCT s FROM Survey s " +
           "LEFT JOIN FETCH s.questions q " +
           "LEFT JOIN FETCH q.options " +
           "WHERE s.id = :surveyId " +
           "ORDER BY q.orderIndex")
    Optional<Survey> findByIdWithQuestionsAndOptions(@Param("surveyId") Long surveyId);

    /**
     * 활성화된 설문과 질문, 선택지 모두 조회
     */
    @Query("SELECT DISTINCT s FROM Survey s " +
           "LEFT JOIN FETCH s.questions q " +
           "LEFT JOIN FETCH q.options " +
           "WHERE s.active = true " +
           "ORDER BY s.id, q.orderIndex")
    List<Survey> findAllActiveWithQuestionsAndOptions();
}