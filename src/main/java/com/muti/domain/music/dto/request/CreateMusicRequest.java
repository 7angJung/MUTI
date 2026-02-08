package com.muti.domain.music.dto.request;

import com.muti.domain.music.enums.Genre;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 음악 생성 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMusicRequest {

    @NotBlank(message = "제목은 필수입니다")
    private String title;

    @NotBlank(message = "아티스트는 필수입니다")
    private String artist;

    private String album;

    private Genre genre;

    private String spotifyId;

    private String youtubeId;

    private Integer durationMs;

    private LocalDate releaseDate;

    private String imageUrl;

    private String previewUrl;

    private String description;
}