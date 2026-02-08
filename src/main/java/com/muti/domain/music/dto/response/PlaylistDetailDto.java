package com.muti.domain.music.dto.response;

import com.muti.domain.music.entity.Playlist;
import com.muti.domain.music.entity.PlaylistMusic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 플레이리스트 상세 응답 DTO (음악 목록 포함)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDetailDto {

    private Long id;
    private String name;
    private String description;
    private Long userId;
    private String username;
    private Boolean isPublic;
    private String mutiType;
    private String imageUrl;
    private Boolean isOwner;
    private List<PlaylistMusicDto> musics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PlaylistDetailDto from(Playlist playlist, List<PlaylistMusic> playlistMusics, boolean isOwner) {
        List<PlaylistMusicDto> musicDtos = playlistMusics.stream()
                .map(PlaylistMusicDto::from)
                .collect(Collectors.toList());

        return PlaylistDetailDto.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .userId(playlist.getUser().getId())
                .username(playlist.getUser().getUsername())
                .isPublic(playlist.getIsPublic())
                .mutiType(playlist.getMutiType() != null ? playlist.getMutiType().name() : null)
                .imageUrl(playlist.getImageUrl())
                .isOwner(isOwner)
                .musics(musicDtos)
                .createdAt(playlist.getCreatedAt())
                .updatedAt(playlist.getUpdatedAt())
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaylistMusicDto {
        private Long id;
        private Integer orderIndex;
        private MusicDto music;
        private LocalDateTime addedAt;

        public static PlaylistMusicDto from(PlaylistMusic playlistMusic) {
            return PlaylistMusicDto.builder()
                    .id(playlistMusic.getId())
                    .orderIndex(playlistMusic.getOrderIndex())
                    .music(MusicDto.from(playlistMusic.getMusic()))
                    .addedAt(playlistMusic.getCreatedAt())
                    .build();
        }
    }
}