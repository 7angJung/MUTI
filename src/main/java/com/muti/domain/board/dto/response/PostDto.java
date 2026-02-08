package com.muti.domain.board.dto.response;

import com.muti.domain.board.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시글 응답 DTO (목록용)
 *
 * @author Claude Sonnet 4.5
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {

    private Long postId;
    private Long boardId;
    private String boardName;
    private Long userId;
    private String username;
    private String title;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity → DTO 변환 (목록용)
     *
     * @param post Post 엔티티
     * @return PostDto
     */
    public static PostDto from(Post post) {
        return PostDto.builder()
                .postId(post.getId())
                .boardId(post.getBoard().getId())
                .boardName(post.getBoard().getName())
                .userId(post.getUser().getId())
                .username(post.getUser().getUsername())
                .title(post.getTitle())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}