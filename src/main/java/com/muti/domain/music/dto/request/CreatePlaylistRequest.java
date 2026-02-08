package com.muti.domain.music.dto.request;

import com.muti.domain.survey.enums.MutiType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 플레이리스트 생성 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlaylistRequest {

    @NotBlank(message = "플레이리스트 이름은 필수입니다")
    private String name;

    private String description;

    private Boolean isPublic;

    private MutiType mutiType;

    private String imageUrl;
}