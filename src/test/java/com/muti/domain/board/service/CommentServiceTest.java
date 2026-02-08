package com.muti.domain.board.service;

import com.muti.domain.auth.entity.User;
import com.muti.domain.auth.entity.UserRole;
import com.muti.domain.auth.repository.UserRepository;
import com.muti.domain.board.dto.request.CreateCommentRequest;
import com.muti.domain.board.dto.response.CommentDto;
import com.muti.domain.board.entity.Board;
import com.muti.domain.board.entity.Comment;
import com.muti.domain.board.entity.Post;
import com.muti.domain.board.enums.BoardType;
import com.muti.domain.board.repository.CommentRepository;
import com.muti.domain.board.repository.PostRepository;
import com.muti.global.error.BusinessException;
import com.muti.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * CommentService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService 테스트")
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("댓글 작성 - 성공")
    void createComment_Success() {
        // given
        Long postId = 1L;
        Long userId = 1L;

        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("테스트 댓글입니다")
                .parentCommentId(null)
                .build();

        Board board = Board.builder().id(1L).name("게시판").boardType(BoardType.FREE).build();
        User user = User.builder().id(userId).email("test@example.com").username("테스터").role(UserRole.USER).build();
        Post post = Post.builder().id(postId).board(board).user(user).title("제목").content("내용").build();

        Comment savedComment = Comment.builder()
                .id(1L)
                .post(post)
                .user(user)
                .content("테스트 댓글입니다")
                .parentComment(null)
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(commentRepository.save(any(Comment.class))).willReturn(savedComment);

        // when
        CommentDto result = commentService.createComment(postId, request, userId);

        // then
        assertThat(result.getContent()).isEqualTo("테스트 댓글입니다");

        verify(postRepository).findById(postId);
        verify(userRepository).findById(userId);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("대댓글 작성 - 부모 댓글이 다른 게시글에 속함")
    void createReply_ParentMismatch() {
        // given
        Long postId = 1L;
        Long userId = 1L;
        Long parentCommentId = 1L;

        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("대댓글")
                .parentCommentId(parentCommentId)
                .build();

        Board board = Board.builder().id(1L).name("게시판").boardType(BoardType.FREE).build();
        User user = User.builder().id(userId).email("test@example.com").username("테스터").role(UserRole.USER).build();
        Post post1 = Post.builder().id(postId).board(board).user(user).title("게시글1").content("내용").build();
        Post post2 = Post.builder().id(2L).board(board).user(user).title("게시글2").content("내용").build();

        Comment parentComment = Comment.builder()
                .id(parentCommentId)
                .post(post2) // 다른 게시글
                .user(user)
                .content("원댓글")
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post1));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(commentRepository.findById(parentCommentId)).willReturn(Optional.of(parentComment));

        // when & then
        assertThatThrownBy(() -> commentService.createComment(postId, request, userId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_PARENT_MISMATCH);

        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("댓글 삭제 - 성공")
    void deleteComment_Success() {
        // given
        Long commentId = 1L;
        Long userId = 1L;

        Board board = Board.builder().id(1L).name("게시판").boardType(BoardType.FREE).build();
        User user = User.builder().id(userId).email("test@example.com").username("테스터").role(UserRole.USER).build();
        Post post = Post.builder().id(1L).board(board).user(user).title("제목").content("내용").build();

        Comment comment = Comment.builder()
                .id(commentId)
                .post(post)
                .user(user)
                .content("댓글")
                .build();

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        commentService.deleteComment(commentId, userId);

        // then
        verify(commentRepository).findById(commentId);
        verify(commentRepository).delete(comment);
    }

    @Test
    @DisplayName("댓글 삭제 - 권한 없음")
    void deleteComment_Forbidden() {
        // given
        Long commentId = 1L;
        Long authorId = 1L;
        Long otherUserId = 2L;

        Board board = Board.builder().id(1L).name("게시판").boardType(BoardType.FREE).build();
        User author = User.builder().id(authorId).email("author@example.com").username("작성자").role(UserRole.USER).build();
        Post post = Post.builder().id(1L).board(board).user(author).title("제목").content("내용").build();

        Comment comment = Comment.builder()
                .id(commentId)
                .post(post)
                .user(author)
                .content("댓글")
                .build();

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(commentId, otherUserId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_DELETE_FORBIDDEN);

        verify(commentRepository).findById(commentId);
        verify(commentRepository, never()).delete(any());
    }

    @Test
    @DisplayName("게시글별 댓글 조회 - 성공")
    void getCommentsByPost_Success() {
        // given
        Long postId = 1L;
        Long userId = 1L;

        Board board = Board.builder().id(1L).name("게시판").boardType(BoardType.FREE).build();
        User user = User.builder().id(userId).email("test@example.com").username("테스터").role(UserRole.USER).build();
        Post post = Post.builder().id(postId).board(board).user(user).title("제목").content("내용").build();

        Comment comment1 = Comment.builder()
                .id(1L)
                .post(post)
                .user(user)
                .content("댓글1")
                .parentComment(null)
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(commentRepository.findByPostIdWithUserOrderByHierarchy(postId))
                .willReturn(List.of(comment1));

        // when
        List<CommentDto> results = commentService.getCommentsByPost(postId, userId);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getContent()).isEqualTo("댓글1");

        verify(postRepository).findById(postId);
        verify(commentRepository).findByPostIdWithUserOrderByHierarchy(postId);
    }

    @Test
    @DisplayName("존재하지 않는 댓글 삭제 - 실패")
    void deleteComment_NotFound() {
        // given
        given(commentRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(999L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_NOT_FOUND);

        verify(commentRepository).findById(999L);
    }
}