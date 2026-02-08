package com.muti.domain.board.dto.response;

import com.muti.domain.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시판 응답 DTO
 *
 * @author Claude Sonnet 4.5
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDto {

    private Long boardId;
    private String name;
    private String description;
    private String boardType;
    private String mutiType;
    private LocalDateTime createdAt;

    /**
     * Entity → DTO 변환
     *
     * @param board Board 엔티티
     * @return BoardDto
     */
    public static BoardDto from(Board board) {
        return BoardDto.builder()
                .boardId(board.getId())
                .name(board.getName())
                .description(board.getDescription())
                .boardType(board.getBoardType().name())
                .mutiType(board.getMutiType() != null ? board.getMutiType().name() : null)
                .createdAt(board.getCreatedAt())
                .build();
    }
}