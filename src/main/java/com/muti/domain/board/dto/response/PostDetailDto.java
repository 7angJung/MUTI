package com.muti.domain.board.dto.response;

import com.muti.domain.board.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시글 상세 응답 DTO (상세 조회용)
 *
 * @author Claude Sonnet 4.5
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDetailDto {

    private Long postId;
    private Long boardId;
    private String boardName;
    private Long userId;
    private String username;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isLiked;  // 현재 사용자의 좋아요 여부
    private Boolean isAuthor; // 현재 사용자가 작성자인지 여부
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity → DTO 변환 (상세용)
     *
     * @param post     Post 엔티티
     * @param isLiked  현재 사용자의 좋아요 여부
     * @param isAuthor 현재 사용자가 작성자인지 여부
     * @return PostDetailDto
     */
    public static PostDetailDto from(Post post, boolean isLiked, boolean isAuthor) {
        return PostDetailDto.builder()
                .postId(post.getId())
                .boardId(post.getBoard().getId())
                .boardName(post.getBoard().getName())
                .userId(post.getUser().getId())
                .username(post.getUser().getUsername())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .isLiked(isLiked)
                .isAuthor(isAuthor)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}