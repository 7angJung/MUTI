package com.muti.domain.board.repository;

import com.muti.domain.board.entity.Board;
import com.muti.domain.board.enums.BoardType;
import com.muti.domain.survey.enums.MutiType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Board Repository
 *
 * 역할: 게시판 데이터 접근 계층
 *
 * @author Claude Sonnet 4.5
 */
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    /**
     * 게시판 타입별 조회
     *
     * @param boardType 게시판 타입
     * @return 해당 타입의 게시판 목록
     */
    List<Board> findByBoardType(BoardType boardType);

    /**
     * MUTI 타입별 게시판 조회
     *
     * @param mutiType MUTI 타입
     * @return 해당 MUTI 타입 게시판
     */
    Optional<Board> findByMutiType(MutiType mutiType);

    /**
     * 게시판 이름으로 조회
     *
     * @param name 게시판 이름
     * @return 해당 이름의 게시판
     */
    Optional<Board> findByName(String name);
}