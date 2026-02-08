package com.muti.domain.music.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 플레이리스트에 음악 추가 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMusicToPlaylistRequest {

    @NotNull(message = "음악 ID는 필수입니다")
    private Long musicId;

    private Integer orderIndex; // null이면 맨 뒤에 추가
}