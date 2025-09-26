package com.muti.muti.survey.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class SurveySubmissionDto {
    // Key: Question ID, Value: Chosen Choice ID
    private Map<Long, Long> answers;
}
