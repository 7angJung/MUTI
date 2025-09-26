package com.muti.muti.survey.dto;

import com.muti.muti.survey.domain.Choice;
import lombok.Getter;

@Getter
public class ChoiceDto {
    private Long id;
    private String text;

    public ChoiceDto(Choice choice) {
        this.id = choice.getId();
        this.text = choice.getText();
    }
}
