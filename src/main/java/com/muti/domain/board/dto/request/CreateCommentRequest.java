package com.muti.domain.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 작성 요청 DTO
 *
 * @author Claude Sonnet 4.5
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentRequest {

    /**
     * 부모 댓글 ID (대댓글인 경우에만 값 존재)
     */
    private Long parentCommentId;

    @NotBlank(message = "내용은 필수입니다")
    @Size(min = 1, max = 1000, message = "내용은 1~1000자여야 합니다")
    private String content;
}