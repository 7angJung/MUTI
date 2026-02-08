package com.muti.domain.music.dto.response;

import com.muti.domain.music.entity.Playlist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 플레이리스트 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDto {

    private Long id;
    private String name;
    private String description;
    private Long userId;
    private String username;
    private Boolean isPublic;
    private String mutiType;
    private String imageUrl;
    private Integer musicCount;
    private Boolean isOwner; // 현재 사용자가 소유자인지
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PlaylistDto from(Playlist playlist, boolean isOwner) {
        return PlaylistDto.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .userId(playlist.getUser().getId())
                .username(playlist.getUser().getUsername())
                .isPublic(playlist.getIsPublic())
                .mutiType(playlist.getMutiType() != null ? playlist.getMutiType().name() : null)
                .imageUrl(playlist.getImageUrl())
                .musicCount(playlist.getMusicCount())
                .isOwner(isOwner)
                .createdAt(playlist.getCreatedAt())
                .updatedAt(playlist.getUpdatedAt())
                .build();
    }

    public static PlaylistDto from(Playlist playlist) {
        return from(playlist, false);
    }
}