package com.muti.domain.board.repository;

import com.muti.config.TestJpaConfig;
import com.muti.domain.auth.entity.User;
import com.muti.domain.auth.entity.UserRole;
import com.muti.domain.board.entity.Board;
import com.muti.domain.board.entity.Post;
import com.muti.domain.board.enums.BoardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * PostRepository 통합 테스트
 */
@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
@DisplayName("PostRepository 통합 테스트")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TestEntityManager em;

    private Board board;
    private User user;

    @BeforeEach
    void setUp() {
        // 테스트용 게시판 생성
        board = Board.builder()
                .name("테스트게시판")
                .boardType(BoardType.FREE)
                .build();
        em.persist(board);

        // 테스트용 사용자 생성
        user = User.builder()
                .email("test@example.com")
                .password("password123")
                .username("테스터")
                .role(UserRole.USER)
                .build();
        em.persist(user);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("게시글 저장 및 조회")
    void savePost() {
        // given
        Post post = Post.builder()
                .board(board)
                .user(user)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        // when
        Post saved = postRepository.save(post);
        em.flush();
        em.clear();

        // then
        Optional<Post> found = postRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("테스트 제목");
        assertThat(found.get().getContent()).isEqualTo("테스트 내용");
        assertThat(found.get().getViewCount()).isEqualTo(0);
        assertThat(found.get().getLikeCount()).isEqualTo(0);
        assertThat(found.get().getCommentCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("게시판별 게시글 조회 (페이징)")
    void findByBoardIdWithPaging() {
        // given
        for (int i = 1; i <= 25; i++) {
            Post post = Post.builder()
                    .board(board)
                    .user(user)
                    .title("제목 " + i)
                    .content("내용 " + i)
                    .build();
            em.persist(post);
        }
        em.flush();
        em.clear();

        // when
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> page = postRepository.findByBoardId(board.getId(), pageRequest);

        // then
        assertThat(page.getTotalElements()).isEqualTo(25);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(20);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    @DisplayName("사용자별 게시글 조회")
    void findByUserId() {
        // given
        User anotherUser = User.builder()
                .email("another@example.com")
                .password("password")
                .username("다른사용자")
                .role(UserRole.USER)
                .build();
        em.persist(anotherUser);

        Post post1 = Post.builder().board(board).user(user).title("내 글 1").content("내용").build();
        Post post2 = Post.builder().board(board).user(user).title("내 글 2").content("내용").build();
        Post post3 = Post.builder().board(board).user(anotherUser).title("다른 사람 글").content("내용").build();

        em.persist(post1);
        em.persist(post2);
        em.persist(post3);
        em.flush();
        em.clear();

        // when
        List<Post> myPosts = postRepository.findByUserId(user.getId());

        // then
        assertThat(myPosts).hasSize(2);
        assertThat(myPosts).extracting(Post::getTitle)
                .containsExactlyInAnyOrder("내 글 1", "내 글 2");
    }

    @Test
    @DisplayName("게시글 조회수 증가")
    void incrementViewCount() {
        // given
        Post post = Post.builder()
                .board(board)
                .user(user)
                .title("제목")
                .content("내용")
                .build();
        Post saved = postRepository.save(post);
        em.flush();
        em.clear();

        // when
        Post found = postRepository.findById(saved.getId()).get();
        found.incrementViewCount();
        found.incrementViewCount();
        postRepository.save(found);
        em.flush();
        em.clear();

        // then
        Post updated = postRepository.findById(saved.getId()).get();
        assertThat(updated.getViewCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("게시글 좋아요 수 증가/감소")
    void updateLikeCount() {
        // given
        Post post = Post.builder()
                .board(board)
                .user(user)
                .title("제목")
                .content("내용")
                .build();
        Post saved = postRepository.save(post);
        em.flush();
        em.clear();

        // when - 좋아요 증가
        Post found = postRepository.findById(saved.getId()).get();
        found.incrementLikeCount();
        postRepository.save(found);
        em.flush();
        em.clear();

        // then
        Post afterIncrement = postRepository.findById(saved.getId()).get();
        assertThat(afterIncrement.getLikeCount()).isEqualTo(1);

        // when - 좋아요 감소
        afterIncrement.decrementLikeCount();
        postRepository.save(afterIncrement);
        em.flush();
        em.clear();

        // then
        Post afterDecrement = postRepository.findById(saved.getId()).get();
        assertThat(afterDecrement.getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("게시글 댓글 수 증가/감소")
    void updateCommentCount() {
        // given
        Post post = Post.builder()
                .board(board)
                .user(user)
                .title("제목")
                .content("내용")
                .build();
        Post saved = postRepository.save(post);
        em.flush();
        em.clear();

        // when - 댓글 수 증가
        Post found = postRepository.findById(saved.getId()).get();
        found.incrementCommentCount();
        found.incrementCommentCount();
        postRepository.save(found);
        em.flush();
        em.clear();

        // then
        Post afterIncrement = postRepository.findById(saved.getId()).get();
        assertThat(afterIncrement.getCommentCount()).isEqualTo(2);

        // when - 댓글 수 감소
        afterIncrement.decrementCommentCount();
        postRepository.save(afterIncrement);
        em.flush();
        em.clear();

        // then
        Post afterDecrement = postRepository.findById(saved.getId()).get();
        assertThat(afterDecrement.getCommentCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글 수정")
    void updatePost() {
        // given
        Post post = Post.builder()
                .board(board)
                .user(user)
                .title("원래 제목")
                .content("원래 내용")
                .build();
        Post saved = postRepository.save(post);
        em.flush();
        em.clear();

        // when
        Post found = postRepository.findById(saved.getId()).get();
        found.update("수정된 제목", "수정된 내용");
        postRepository.save(found);
        em.flush();
        em.clear();

        // then
        Post updated = postRepository.findById(saved.getId()).get();
        assertThat(updated.getTitle()).isEqualTo("수정된 제목");
        assertThat(updated.getContent()).isEqualTo("수정된 내용");
        assertThat(updated.getUpdatedAt()).isAfter(updated.getCreatedAt());
    }

    @Test
    @DisplayName("게시글 삭제")
    void deletePost() {
        // given
        Post post = Post.builder()
                .board(board)
                .user(user)
                .title("제목")
                .content("내용")
                .build();
        Post saved = postRepository.save(post);
        em.flush();
        em.clear();

        // when
        postRepository.deleteById(saved.getId());
        em.flush();

        // then
        Optional<Post> result = postRepository.findById(saved.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("작성자 확인")
    void isAuthor() {
        // given
        Post post = Post.builder()
                .board(board)
                .user(user)
                .title("제목")
                .content("내용")
                .build();
        Post saved = postRepository.save(post);

        User anotherUser = User.builder()
                .email("another@example.com")
                .password("password")
                .username("다른사용자")
                .role(UserRole.USER)
                .build();
        em.persist(anotherUser);
        em.flush();

        // when & then
        assertThat(saved.isAuthor(user.getId())).isTrue();
        assertThat(saved.isAuthor(anotherUser.getId())).isFalse();
        assertThat(saved.isAuthor(99999L)).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회 - 실패")
    void findByIdNotFound() {
        // when
        Optional<Post> result = postRepository.findById(99999L);

        // then
        assertThat(result).isEmpty();
    }
}