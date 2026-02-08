package com.muti.domain.board.dto.response;

import com.muti.domain.board.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 댓글 응답 DTO
 *
 * @author Claude Sonnet 4.5
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long commentId;
    private Long postId;
    private Long userId;
    private String username;
    private Long parentCommentId;
    private String content;
    private Boolean isAuthor; // 현재 사용자가 작성자인지 여부
    private LocalDateTime createdAt;

    @Builder.Default
    private List<CommentDto> replies = new ArrayList<>();  // 대댓글 목록

    /**
     * Entity → DTO 변환
     *
     * @param comment  Comment 엔티티
     * @param isAuthor 현재 사용자가 작성자인지 여부
     * @return CommentDto
     */
    public static CommentDto from(Comment comment, boolean isAuthor) {
        return CommentDto.builder()
                .commentId(comment.getId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .parentCommentId(comment.getParentComment() != null ?
                        comment.getParentComment().getId() : null)
                .content(comment.getContent())
                .isAuthor(isAuthor)
                .createdAt(comment.getCreatedAt())
                .build();
    }

    /**
     * 대댓글 추가
     *
     * @param reply 대댓글 DTO
     */
    public void addReply(CommentDto reply) {
        this.replies.add(reply);
    }
}