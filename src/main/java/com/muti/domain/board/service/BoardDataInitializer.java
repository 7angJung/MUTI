package com.muti.domain.board.service;

import com.muti.domain.board.entity.Board;
import com.muti.domain.board.enums.BoardType;
import com.muti.domain.board.repository.BoardRepository;
import com.muti.domain.survey.enums.MutiType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시판 초기 데이터 생성기
 *
 * 역할: 애플리케이션 시작 시 기본 게시판 데이터가 없으면 자동 생성
 *
 * 생성 데이터:
 * - 자유게시판 1개
 * - MUTI 타입별 게시판 16개 (ESAP, ESAU, ESDP, ESDU, EFAP, EFAU, EFDP, EFDU,
 *                          ISAP, ISAU, ISDP, ISDU, IFAP, IFAU, IFDP, IFDU)
 *
 * 실행 시점: ApplicationRunner 구현으로 Spring Boot 시작 후 자동 실행
 *
 * @author Claude Sonnet 4.5
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BoardDataInitializer implements ApplicationRunner {

    private final BoardRepository boardRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // 이미 게시판이 존재하면 초기화하지 않음
        if (boardRepository.count() > 0) {
            log.info("Boards already exist. Skipping board data initialization.");
            return;
        }

        log.info("Starting board data initialization...");

        // 자유게시판 생성
        createFreeBoard();

        // MUTI 타입별 게시판 16개 생성
        createMutiTypeBoards();

        long totalBoards = boardRepository.count();
        log.info("Board data initialization completed: {} boards created", totalBoards);
    }

    /**
     * 자유게시판 생성
     */
    private void createFreeBoard() {
        Board freeBoard = Board.builder()
                .name("자유게시판")
                .description("자유롭게 이야기를 나눠보세요!")
                .boardType(BoardType.FREE)
                .mutiType(null)
                .build();

        boardRepository.save(freeBoard);
        log.debug("Created free board: {}", freeBoard.getName());
    }

    /**
     * MUTI 타입별 게시판 16개 생성
     */
    private void createMutiTypeBoards() {
        // E-S 조합
        createMutiTypeBoard(MutiType.ESAP, "감성적이고 잔잔한 어쿠스틱 대중 음악을 좋아하는 분들의 공간");
        createMutiTypeBoard(MutiType.ESAU, "감성적이고 잔잔한 어쿠스틱 실험 음악을 좋아하는 분들의 공간");
        createMutiTypeBoard(MutiType.ESDP, "감성적이고 잔잔한 디지털 대중 음악을 좋아하는 분들의 공간");
        createMutiTypeBoard(MutiType.ESDU, "감성적이고 잔잔한 디지털 실험 음악을 좋아하는 분들의 공간");

        // E-F 조합
        createMutiTypeBoard(MutiType.EFAP, "감성적이고 빠른 어쿠스틱 대중 음악을 좋아하는 분들의 공간");
        createMutiTypeBoard(MutiType.EFAU, "감성적이고 빠른 어쿠스틱 실험 음악을 좋아하는 분들의 공간");
        createMutiTypeBoard(MutiType.EFDP, "감성적이고 빠른 디지털 대중 음악을 좋아하는 분들의 공간");
        createMutiTypeBoard(MutiType.EFDU, "감성적이고 빠른 디지털 실험 음악을 좋아하는 분들의 공간");

        // I-S 조합
        createMutiTypeBoard(MutiType.ISAP, "연주 중심의 잔잔한 어쿠스틱 대중 음악을 좋아하는 분들의 공간");
        createMutiTypeBoard(MutiType.ISAU, "연주 중심의 잔잔한 어쿠스틱 실험 음악을 좋아하는 분들의 공간");
        createMutiTypeBoard(MutiType.ISDP, "연주 중심의 잔잔한 디지털 대중 음악을 좋아하는 분들의 공간");
        createMutiTypeBoard(MutiType.ISDU, "연주 중심의 잔잔한 디지털 실험 음악을 좋아하는 분들의 공간");

        // I-F 조합
        createMutiTypeBoard(MutiType.IFAP, "연주 중심의 빠른 어쿠스틱 대중 음악을 좋아하는 분들의 공간");
        createMutiTypeBoard(MutiType.IFAU, "연주 중심의 빠른 어쿠스틱 실험 음악을 좋아하는 분들의 공간");
        createMutiTypeBoard(MutiType.IFDP, "연주 중심의 빠른 디지털 대중 음악을 좋아하는 분들의 공간");
        createMutiTypeBoard(MutiType.IFDU, "연주 중심의 빠른 디지털 실험 음악을 좋아하는 분들의 공간");
    }

    /**
     * MUTI 타입 게시판 생성 헬퍼 메서드
     */
    private void createMutiTypeBoard(MutiType mutiType, String description) {
        Board board = Board.builder()
                .name(mutiType.name() + " 게시판")
                .description(description)
                .boardType(BoardType.MUTI_TYPE)
                .mutiType(mutiType)
                .build();

        boardRepository.save(board);
        log.debug("Created MUTI type board: {}", board.getName());
    }
}