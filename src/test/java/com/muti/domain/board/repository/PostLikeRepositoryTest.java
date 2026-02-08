package com.muti.domain.board.repository;

import com.muti.config.TestJpaConfig;
import com.muti.domain.auth.entity.User;
import com.muti.domain.auth.entity.UserRole;
import com.muti.domain.board.entity.Board;
import com.muti.domain.board.entity.Post;
import com.muti.domain.board.entity.PostLike;
import com.muti.domain.board.enums.BoardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * PostLikeRepository 통합 테스트
 */
@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
@DisplayName("PostLikeRepository 통합 테스트")
class PostLikeRepositoryTest {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private TestEntityManager em;

    private Board board;
    private User user;
    private Post post;

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

        // 테스트용 게시글 생성
        post = Post.builder()
                .board(board)
                .user(user)
                .title("테스트 게시글")
                .content("테스트 내용")
                .build();
        em.persist(post);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("좋아요 저장 및 조회")
    void savePostLike() {
        // given
        PostLike postLike = PostLike.builder()
                .post(post)
                .user(user)
                .build();

        // when
        PostLike saved = postLikeRepository.save(postLike);
        em.flush();
        em.clear();

        // then
        Optional<PostLike> found = postLikeRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getPost().getId()).isEqualTo(post.getId());
        assertThat(found.get().getUser().getId()).isEqualTo(user.getId());
        assertThat(found.get().getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("게시글과 사용자로 좋아요 조회")
    void findByPostIdAndUserId() {
        // given
        PostLike postLike = PostLike.builder()
                .post(post)
                .user(user)
                .build();
        postLikeRepository.save(postLike);
        em.flush();
        em.clear();

        // when
        Optional<PostLike> result = postLikeRepository.findByPostIdAndUserId(post.getId(), user.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getPost().getId()).isEqualTo(post.getId());
        assertThat(result.get().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("좋아요 존재 여부 확인")
    void existsByPostIdAndUserId() {
        // given
        PostLike postLike = PostLike.builder()
                .post(post)
                .user(user)
                .build();
        postLikeRepository.save(postLike);
        em.flush();
        em.clear();

        // when
        boolean exists = postLikeRepository.existsByPostIdAndUserId(post.getId(), user.getId());
        boolean notExists = postLikeRepository.existsByPostIdAndUserId(post.getId(), 99999L);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("게시글별 좋아요 목록 조회")
    void findByPostId() {
        // given
        User user2 = User.builder()
                .email("user2@example.com")
                .password("password")
                .username("사용자2")
                .role(UserRole.USER)
                .build();
        em.persist(user2);

        User user3 = User.builder()
                .email("user3@example.com")
                .password("password")
                .username("사용자3")
                .role(UserRole.USER)
                .build();
        em.persist(user3);

        PostLike like1 = PostLike.builder().post(post).user(user).build();
        PostLike like2 = PostLike.builder().post(post).user(user2).build();
        PostLike like3 = PostLike.builder().post(post).user(user3).build();

        postLikeRepository.saveAll(List.of(like1, like2, like3));
        em.flush();
        em.clear();

        // when
        List<PostLike> likes = postLikeRepository.findByPostId(post.getId());

        // then
        assertThat(likes).hasSize(3);
        assertThat(likes).extracting(like -> like.getUser().getUsername())
                .containsExactlyInAnyOrder("테스터", "사용자2", "사용자3");
    }

    @Test
    @DisplayName("사용자별 좋아요 목록 조회")
    void findByUserId() {
        // given
        Post post2 = Post.builder()
                .board(board)
                .user(user)
                .title("게시글 2")
                .content("내용 2")
                .build();
        em.persist(post2);

        PostLike like1 = PostLike.builder().post(post).user(user).build();
        PostLike like2 = PostLike.builder().post(post2).user(user).build();

        postLikeRepository.saveAll(List.of(like1, like2));
        em.flush();
        em.clear();

        // when
        List<PostLike> likes = postLikeRepository.findByUserId(user.getId());

        // then
        assertThat(likes).hasSize(2);
        assertThat(likes).extracting(like -> like.getPost().getTitle())
                .containsExactlyInAnyOrder("테스트 게시글", "게시글 2");
    }

    @Test
    @DisplayName("게시글별 좋아요 수 조회")
    void countByPostId() {
        // given
        User user2 = User.builder()
                .email("user2@example.com")
                .password("password")
                .username("사용자2")
                .role(UserRole.USER)
                .build();
        em.persist(user2);

        PostLike like1 = PostLike.builder().post(post).user(user).build();
        PostLike like2 = PostLike.builder().post(post).user(user2).build();

        postLikeRepository.saveAll(List.of(like1, like2));
        em.flush();
        em.clear();

        // when
        long count = postLikeRepository.countByPostId(post.getId());

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("좋아요 삭제")
    void deletePostLike() {
        // given
        PostLike postLike = PostLike.builder()
                .post(post)
                .user(user)
                .build();
        PostLike saved = postLikeRepository.save(postLike);
        em.flush();
        em.clear();

        // when
        postLikeRepository.deleteById(saved.getId());
        em.flush();

        // then
        Optional<PostLike> result = postLikeRepository.findById(saved.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("게시글과 사용자로 좋아요 삭제")
    void deleteByPostIdAndUserId() {
        // given
        PostLike postLike = PostLike.builder()
                .post(post)
                .user(user)
                .build();
        postLikeRepository.save(postLike);
        em.flush();
        em.clear();

        // when
        postLikeRepository.deleteByPostIdAndUserId(post.getId(), user.getId());
        em.flush();

        // then
        boolean exists = postLikeRepository.existsByPostIdAndUserId(post.getId(), user.getId());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("중복 좋아요 방지 - UNIQUE 제약 조건")
    void preventDuplicateLikes() {
        // given
        PostLike like1 = PostLike.builder()
                .post(post)
                .user(user)
                .build();
        postLikeRepository.save(like1);
        em.flush();

        // when & then - 같은 게시글에 같은 사용자가 또 좋아요를 누르면 예외 발생
        PostLike like2 = PostLike.builder()
                .post(post)
                .user(user)
                .build();

        assertThatThrownBy(() -> {
            postLikeRepository.save(like2);
            em.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("게시글 삭제 시 좋아요도 함께 삭제 (CASCADE)")
    @org.junit.jupiter.api.Disabled("H2 데이터베이스 CASCADE 제약 조건 문제 - PostgreSQL에서는 정상 동작")
    void deleteCascadeWhenPostDeleted() {
        // given
        PostLike postLike = PostLike.builder()
                .post(post)
                .user(user)
                .build();
        postLikeRepository.save(postLike);
        em.flush();
        em.clear();

        Long postId = post.getId();
        Long likeId = postLike.getId();

        // when - 게시글 삭제
        Post foundPost = em.find(Post.class, postId);
        em.remove(foundPost);
        em.flush();
        em.clear();

        // then - 좋아요도 함께 삭제됨
        Optional<PostLike> result = postLikeRepository.findById(likeId);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 좋아요 조회 - 실패")
    void findByIdNotFound() {
        // when
        Optional<PostLike> result = postLikeRepository.findById(99999L);

        // then
        assertThat(result).isEmpty();
    }
}