package com.muti.domain.survey.mapper;

import com.muti.domain.survey.dto.response.*;
import com.muti.domain.survey.entity.*;
import com.muti.domain.survey.enums.MutiAxis;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Survey 엔티티 <-> DTO 변환
 */
@Component
public class SurveyMapper {

    /**
     * Survey 엔티티 -> SurveyDto DTO
     */
    public SurveyDto toResponse(Survey survey) {
        return SurveyDto.builder()
                .id(survey.getId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .active(survey.getActive())
                .questions(survey.getQuestions().stream()
                        .sorted(Comparator.comparing(Question::getOrderIndex))
                        .map(this::toQuestionResponse)
                        .collect(Collectors.toList()))
                .createdAt(survey.getCreatedAt())
                .updatedAt(survey.getUpdatedAt())
                .build();
    }

    /**
     * Question 엔티티 -> QuestionDto DTO
     */
    private QuestionDto toQuestionResponse(Question question) {
        return QuestionDto.builder()
                .id(question.getId())
                .content(question.getContent())
                .axis(question.getAxis().name())
                .orderIndex(question.getOrderIndex())
                .options(question.getOptions().stream()
                        .sorted(Comparator.comparing(QuestionOption::getOrderIndex))
                        .map(this::toOptionResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * QuestionOption 엔티티 -> OptionDto DTO
     */
    private OptionDto toOptionResponse(QuestionOption option) {
        return OptionDto.builder()
                .id(option.getId())
                .content(option.getContent())
                .orderIndex(option.getOrderIndex())
                .build();
    }

    /**
     * SurveyResult 엔티티 -> SurveyResultDto DTO
     */
    public SurveyResultDto toResultResponse(SurveyResult result) {
        Map<String, Integer> axisScores = Map.of(
                MutiAxis.E_I.name(), result.getEiScore(),
                MutiAxis.S_F.name(), result.getSfScore(),
                MutiAxis.A_D.name(), result.getAdScore(),
                MutiAxis.P_U.name(), result.getPuScore()
        );

        Map<String, String> axisDirections = Map.of(
                MutiAxis.E_I.name(), result.getEiScore() >= 0 ? "E" : "I",
                MutiAxis.S_F.name(), result.getSfScore() >= 0 ? "S" : "F",
                MutiAxis.A_D.name(), result.getAdScore() >= 0 ? "A" : "D",
                MutiAxis.P_U.name(), result.getPuScore() >= 0 ? "P" : "U"
        );

        return SurveyResultDto.builder()
                .id(result.getId())
                .surveyId(result.getSurvey().getId())
                .mutiType(result.getMutiType().name())
                .mutiTypeName(result.getMutiType().getTypeName())
                .mutiTypeDescription(result.getMutiType().getDescription())
                .axisScores(axisScores)
                .axisDirections(axisDirections)
                .createdAt(result.getCreatedAt())
                .build();
    }
}