package com.muti.domain.music.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 플레이리스트 수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlaylistRequest {

    private String name;

    private String description;

    private Boolean isPublic;
}