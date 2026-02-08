package com.muti.domain.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좋아요 토글 응답 DTO
 *
 * @author Claude Sonnet 4.5
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponse {

    private Boolean isLiked;       // 좋아요 상태 (true: 좋아요, false: 좋아요 취소)
    private Integer likeCount;     // 현재 좋아요 수
}