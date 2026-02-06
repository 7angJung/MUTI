package com.muti.domain.survey.repository;

import com.muti.domain.survey.entity.Question;
import com.muti.domain.survey.enums.MutiAxis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Question Repository
 */
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * 설문 ID로 질문 목록 조회 (순서대로)
     */
    List<Question> findBySurveyIdOrderByOrderIndex(Long surveyId);

    /**
     * 특정 축의 질문 조회
     */
    List<Question> findByAxis(MutiAxis axis);

    /**
     * 설문의 질문과 선택지 함께 조회
     */
    @Query("SELECT DISTINCT q FROM Question q " +
           "LEFT JOIN FETCH q.options o " +
           "WHERE q.survey.id = :surveyId " +
           "ORDER BY q.orderIndex, o.orderIndex")
    List<Question> findBySurveyIdWithOptions(@Param("surveyId") Long surveyId);
}