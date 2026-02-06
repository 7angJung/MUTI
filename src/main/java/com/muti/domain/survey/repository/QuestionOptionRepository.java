package com.muti.domain.survey.repository;

import com.muti.domain.survey.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * QuestionOption Repository
 */
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {

    /**
     * 질문 ID로 선택지 목록 조회 (순서대로)
     */
    List<QuestionOption> findByQuestionIdOrderByOrderIndex(Long questionId);
}