package com.muti.domain.board.service;

import com.muti.domain.board.dto.response.BoardDto;
import com.muti.domain.board.entity.Board;
import com.muti.domain.board.repository.BoardRepository;
import com.muti.global.error.BusinessException;
import com.muti.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시판 서비스
 *
 * 역할: 게시판 조회 비즈니스 로직 처리
 *
 * @author Claude Sonnet 4.5
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    /**
     * 모든 게시판 조회
     *
     * @return 게시판 목록
     */
    public List<BoardDto> getAllBoards() {
        log.info("전체 게시판 조회");

        List<Board> boards = boardRepository.findAll();

        log.info("게시판 조회 완료: count={}", boards.size());

        return boards.stream()
                .map(BoardDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 게시판 ID로 조회 (내부 사용)
     *
     * @param boardId 게시판 ID
     * @return Board 엔티티
     * @throws BusinessException 게시판을 찾을 수 없는 경우
     */
    public Board getBoardEntity(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> {
                    log.warn("게시판을 찾을 수 없습니다: boardId={}", boardId);
                    return new BusinessException(ErrorCode.BOARD_NOT_FOUND);
                });
    }
}