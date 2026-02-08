package com.muti.domain.music.dto.request;

import com.muti.domain.music.enums.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 음악 수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMusicRequest {

    private String title;

    private String artist;

    private String album;

    private Genre genre;

    private String description;
}