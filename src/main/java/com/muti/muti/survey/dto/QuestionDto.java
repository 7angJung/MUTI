package com.muti.muti.survey.dto;

import com.muti.muti.survey.domain.Question;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class QuestionDto {
    private Long id;
    private String text;
    private List<ChoiceDto> choices;

    public QuestionDto(Question question) {
        this.id = question.getId();
        this.text = question.getText();
        this.choices = question.getChoices().stream().map(ChoiceDto::new).collect(Collectors.toList());
    }
}
