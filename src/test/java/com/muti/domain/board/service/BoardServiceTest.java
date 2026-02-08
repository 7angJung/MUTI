package com.muti.domain.board.service;

import com.muti.domain.board.dto.response.BoardDto;
import com.muti.domain.board.entity.Board;
import com.muti.domain.board.enums.BoardType;
import com.muti.domain.board.repository.BoardRepository;
import com.muti.domain.survey.enums.MutiType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * BoardService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BoardService 테스트")
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;

    @Test
    @DisplayName("모든 게시판 조회 - 성공")
    void getAllBoards_Success() {
        // given
        Board free = Board.builder()
                .id(1L)
                .name("자유게시판")
                .description("자유롭게")
                .boardType(BoardType.FREE)
                .mutiType(null)
                .build();

        Board esap = Board.builder()
                .id(2L)
                .name("ESAP 게시판")
                .description("ESAP")
                .boardType(BoardType.MUTI_TYPE)
                .mutiType(MutiType.ESAP)
                .build();

        given(boardRepository.findAll()).willReturn(List.of(free, esap));

        // when
        List<BoardDto> results = boardService.getAllBoards();

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getName()).isEqualTo("자유게시판");
        assertThat(results.get(0).getBoardType()).isEqualTo("FREE");
        assertThat(results.get(1).getName()).isEqualTo("ESAP 게시판");
        assertThat(results.get(1).getMutiType()).isEqualTo("ESAP");

        verify(boardRepository).findAll();
    }

    @Test
    @DisplayName("게시판이 없을 때 - 빈 리스트 반환")
    void getAllBoards_Empty() {
        // given
        given(boardRepository.findAll()).willReturn(List.of());

        // when
        List<BoardDto> results = boardService.getAllBoards();

        // then
        assertThat(results).isEmpty();
        verify(boardRepository).findAll();
    }
}