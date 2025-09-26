package com.muti.muti.survey.dto;

import com.muti.muti.music.dto.MusicDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SurveyResultWithMusicDto {

    private String mutiType;
    private String description;
    private List<MusicDto> recommendations;

    public SurveyResultWithMusicDto(MutiResultDto mutiResult, List<MusicDto> recommendations) {
        this.mutiType = mutiResult.getMutiType();
        this.description = mutiResult.getDescription();
        this.recommendations = recommendations;
    }
}