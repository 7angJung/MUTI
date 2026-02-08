package com.muti.domain.music.dto.response;

import com.muti.domain.music.entity.Music;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 음악 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicDto {

    private Long id;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String spotifyId;
    private String youtubeId;
    private Integer durationMs;
    private String formattedDuration; // "3:45" 형식
    private LocalDate releaseDate;
    private String imageUrl;
    private String previewUrl;
    private String description;
    private LocalDateTime createdAt;

    public static MusicDto from(Music music) {
        return MusicDto.builder()
                .id(music.getId())
                .title(music.getTitle())
                .artist(music.getArtist())
                .album(music.getAlbum())
                .genre(music.getGenre() != null ? music.getGenre().name() : null)
                .spotifyId(music.getSpotifyId())
                .youtubeId(music.getYoutubeId())
                .durationMs(music.getDurationMs())
                .formattedDuration(music.getFormattedDuration())
                .releaseDate(music.getReleaseDate())
                .imageUrl(music.getImageUrl())
                .previewUrl(music.getPreviewUrl())
                .description(music.getDescription())
                .createdAt(music.getCreatedAt())
                .build();
    }
}