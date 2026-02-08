package com.muti.domain.board.repository;

import com.muti.config.TestJpaConfig;
import com.muti.domain.board.entity.Board;
import com.muti.domain.board.enums.BoardType;
import com.muti.domain.survey.enums.MutiType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * BoardRepository 통합 테스트
 */
@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
@DisplayName("BoardRepository 통합 테스트")
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName("자유게시판 저장 및 조회")
    void saveFreeBoard() {
        // given
        Board board = Board.builder()
                .name("자유게시판")
                .description("자유롭게 이야기를 나눠보세요!")
                .boardType(BoardType.FREE)
                .mutiType(null)
                .build();

        // when
        Board saved = boardRepository.save(board);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("자유게시판");
        assertThat(saved.getBoardType()).isEqualTo(BoardType.FREE);
        assertThat(saved.getMutiType()).isNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("MUTI 타입 게시판 저장 및 조회")
    void saveMutiTypeBoard() {
        // given
        Board board = Board.builder()
                .name("ESAP 게시판")
                .description("ESAP 타입 사용자들의 공간")
                .boardType(BoardType.MUTI_TYPE)
                .mutiType(MutiType.ESAP)
                .build();

        // when
        Board saved = boardRepository.save(board);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("ESAP 게시판");
        assertThat(saved.getBoardType()).isEqualTo(BoardType.MUTI_TYPE);
        assertThat(saved.getMutiType()).isEqualTo(MutiType.ESAP);
    }

    @Test
    @DisplayName("게시판 이름으로 조회")
    void findByName() {
        // given
        Board board = Board.builder()
                .name("테스트게시판")
                .boardType(BoardType.FREE)
                .build();
        boardRepository.save(board);

        // when
        Optional<Board> result = boardRepository.findByName("테스트게시판");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("테스트게시판");
    }

    @Test
    @DisplayName("게시판 타입별 조회")
    void findByBoardType() {
        // given
        Board free = Board.builder().name("자유게시판").boardType(BoardType.FREE).build();
        Board muti1 = Board.builder().name("ESAP").boardType(BoardType.MUTI_TYPE).mutiType(MutiType.ESAP).build();
        Board muti2 = Board.builder().name("IFDU").boardType(BoardType.MUTI_TYPE).mutiType(MutiType.IFDU).build();

        boardRepository.saveAll(List.of(free, muti1, muti2));

        // when
        List<Board> freeBoards = boardRepository.findByBoardType(BoardType.FREE);
        List<Board> mutiBoards = boardRepository.findByBoardType(BoardType.MUTI_TYPE);

        // then
        assertThat(freeBoards).hasSize(1);
        assertThat(freeBoards.get(0).getName()).isEqualTo("자유게시판");

        assertThat(mutiBoards).hasSize(2);
        assertThat(mutiBoards).extracting(Board::getName)
                .containsExactlyInAnyOrder("ESAP", "IFDU");
    }

    @Test
    @DisplayName("MUTI 타입으로 게시판 조회")
    void findByMutiType() {
        // given
        Board esap = Board.builder()
                .name("ESAP 게시판")
                .boardType(BoardType.MUTI_TYPE)
                .mutiType(MutiType.ESAP)
                .build();
        Board ifdu = Board.builder()
                .name("IFDU 게시판")
                .boardType(BoardType.MUTI_TYPE)
                .mutiType(MutiType.IFDU)
                .build();

        boardRepository.saveAll(List.of(esap, ifdu));

        // when
        Optional<Board> result = boardRepository.findByMutiType(MutiType.ESAP);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("ESAP 게시판");
    }

    @Test
    @DisplayName("모든 게시판 조회")
    void findAll() {
        // given
        Board free = Board.builder().name("자유게시판").boardType(BoardType.FREE).build();
        Board esap = Board.builder().name("ESAP").boardType(BoardType.MUTI_TYPE).mutiType(MutiType.ESAP).build();
        Board ifdu = Board.builder().name("IFDU").boardType(BoardType.MUTI_TYPE).mutiType(MutiType.IFDU).build();

        boardRepository.saveAll(List.of(free, esap, ifdu));

        // when
        List<Board> results = boardRepository.findAll();

        // then
        assertThat(results).hasSize(3);
    }

    @Test
    @DisplayName("존재하지 않는 게시판 조회 - 실패")
    void findByNameNotFound() {
        // when
        Optional<Board> result = boardRepository.findByName("없는게시판");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("게시판 삭제")
    void deleteBoard() {
        // given
        Board board = Board.builder()
                .name("삭제될게시판")
                .boardType(BoardType.FREE)
                .build();
        Board saved = boardRepository.save(board);

        // when
        boardRepository.deleteById(saved.getId());

        // then
        Optional<Board> result = boardRepository.findById(saved.getId());
        assertThat(result).isEmpty();
    }
}