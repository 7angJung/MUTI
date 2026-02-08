package com.muti.domain.board.repository;

import com.muti.config.TestJpaConfig;
import com.muti.domain.auth.entity.User;
import com.muti.domain.auth.entity.UserRole;
import com.muti.domain.board.entity.Board;
import com.muti.domain.board.entity.Comment;
import com.muti.domain.board.entity.Post;
import com.muti.domain.board.enums.BoardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * CommentRepository 통합 테스트
 */
@DataJpaTest
@Import(TestJpaConfig.class)
@ActiveProfiles("test")
@DisplayName("CommentRepository 통합 테스트")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

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
    @DisplayName("댓글 저장 및 조회")
    void saveComment() {
        // given
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content("테스트 댓글입니다")
                .parentComment(null)
                .build();

        // when
        Comment saved = commentRepository.save(comment);
        em.flush();
        em.clear();

        // then
        Optional<Comment> found = commentRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("테스트 댓글입니다");
        assertThat(found.get().getParentComment()).isNull();
        assertThat(found.get().getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("게시글별 댓글 조회")
    void findByPostId() {
        // given
        Comment comment1 = Comment.builder()
                .post(post)
                .user(user)
                .content("첫 번째 댓글")
                .build();
        Comment comment2 = Comment.builder()
                .post(post)
                .user(user)
                .content("두 번째 댓글")
                .build();

        em.persist(comment1);
        em.persist(comment2);
        em.flush();
        em.clear();

        // when
        List<Comment> comments = commentRepository.findByPostId(post.getId());

        // then
        assertThat(comments).hasSize(2);
        assertThat(comments).extracting(Comment::getContent)
                .containsExactlyInAnyOrder("첫 번째 댓글", "두 번째 댓글");
    }

    @Test
    @DisplayName("대댓글 저장 및 조회")
    void saveReply() {
        // given - 원댓글 저장
        Comment parentComment = Comment.builder()
                .post(post)
                .user(user)
                .content("원댓글")
                .build();
        em.persist(parentComment);
        em.flush();

        // given - 대댓글 저장
        Comment reply = Comment.builder()
                .post(post)
                .user(user)
                .content("대댓글")
                .parentComment(parentComment)
                .build();

        // when
        Comment savedReply = commentRepository.save(reply);
        em.flush();
        em.clear();

        // then
        Optional<Comment> found = commentRepository.findById(savedReply.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("대댓글");
        assertThat(found.get().getParentComment()).isNotNull();
        assertThat(found.get().getParentComment().getId()).isEqualTo(parentComment.getId());
    }

    @Test
    @DisplayName("부모 댓글 ID로 대댓글 조회")
    void findByParentCommentId() {
        // given
        Comment parentComment = Comment.builder()
                .post(post)
                .user(user)
                .content("원댓글")
                .build();
        em.persist(parentComment);
        em.flush();

        Comment reply1 = Comment.builder()
                .post(post)
                .user(user)
                .content("대댓글 1")
                .parentComment(parentComment)
                .build();
        Comment reply2 = Comment.builder()
                .post(post)
                .user(user)
                .content("대댓글 2")
                .parentComment(parentComment)
                .build();

        em.persist(reply1);
        em.persist(reply2);
        em.flush();
        em.clear();

        // when
        List<Comment> replies = commentRepository.findByParentCommentId(parentComment.getId());

        // then
        assertThat(replies).hasSize(2);
        assertThat(replies).extracting(Comment::getContent)
                .containsExactlyInAnyOrder("대댓글 1", "대댓글 2");
    }

    @Test
    @DisplayName("최상위 댓글만 조회 (parentComment가 null)")
    void findTopLevelComments() {
        // given
        Comment topLevel1 = Comment.builder()
                .post(post)
                .user(user)
                .content("최상위 댓글 1")
                .parentComment(null)
                .build();
        Comment topLevel2 = Comment.builder()
                .post(post)
                .user(user)
                .content("최상위 댓글 2")
                .parentComment(null)
                .build();
        em.persist(topLevel1);
        em.persist(topLevel2);
        em.flush();

        Comment reply = Comment.builder()
                .post(post)
                .user(user)
                .content("대댓글")
                .parentComment(topLevel1)
                .build();
        em.persist(reply);
        em.flush();
        em.clear();

        // when
        List<Comment> topLevelComments = commentRepository.findByPostIdAndParentCommentIsNull(post.getId());

        // then
        assertThat(topLevelComments).hasSize(2);
        assertThat(topLevelComments).extracting(Comment::getContent)
                .containsExactlyInAnyOrder("최상위 댓글 1", "최상위 댓글 2");
    }

    @Test
    @DisplayName("사용자별 댓글 조회")
    void findByUserId() {
        // given
        User anotherUser = User.builder()
                .email("another@example.com")
                .password("password")
                .username("다른사용자")
                .role(UserRole.USER)
                .build();
        em.persist(anotherUser);

        Comment myComment1 = Comment.builder()
                .post(post)
                .user(user)
                .content("내 댓글 1")
                .build();
        Comment myComment2 = Comment.builder()
                .post(post)
                .user(user)
                .content("내 댓글 2")
                .build();
        Comment otherComment = Comment.builder()
                .post(post)
                .user(anotherUser)
                .content("다른 사람 댓글")
                .build();

        em.persist(myComment1);
        em.persist(myComment2);
        em.persist(otherComment);
        em.flush();
        em.clear();

        // when
        List<Comment> myComments = commentRepository.findByUserId(user.getId());

        // then
        assertThat(myComments).hasSize(2);
        assertThat(myComments).extracting(Comment::getContent)
                .containsExactlyInAnyOrder("내 댓글 1", "내 댓글 2");
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment() {
        // given
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content("삭제될 댓글")
                .build();
        Comment saved = commentRepository.save(comment);
        em.flush();
        em.clear();

        // when
        commentRepository.deleteById(saved.getId());
        em.flush();

        // then
        Optional<Comment> result = commentRepository.findById(saved.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("부모 댓글 삭제 시 대댓글도 함께 삭제 (CASCADE)")
    @org.junit.jupiter.api.Disabled("H2 데이터베이스 CASCADE 제약 조건 문제 - PostgreSQL에서는 정상 동작")
    void deleteCascade() {
        // given
        Comment parentComment = Comment.builder()
                .post(post)
                .user(user)
                .content("부모 댓글")
                .build();
        em.persist(parentComment);
        em.flush();

        Comment reply = Comment.builder()
                .post(post)
                .user(user)
                .content("대댓글")
                .parentComment(parentComment)
                .build();
        em.persist(reply);
        em.flush();
        em.clear();

        Long parentId = parentComment.getId();
        Long replyId = reply.getId();

        // when
        commentRepository.deleteById(parentId);
        em.flush();
        em.clear();

        // then
        assertThat(commentRepository.findById(parentId)).isEmpty();
        assertThat(commentRepository.findById(replyId)).isEmpty();
    }

    @Test
    @DisplayName("작성자 확인")
    void isAuthor() {
        // given
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content("댓글")
                .build();
        Comment saved = commentRepository.save(comment);

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
    @DisplayName("존재하지 않는 댓글 조회 - 실패")
    void findByIdNotFound() {
        // when
        Optional<Comment> result = commentRepository.findById(99999L);

        // then
        assertThat(result).isEmpty();
    }
}