package com.muti.domain.board.service;

import com.muti.domain.auth.entity.User;
import com.muti.domain.auth.entity.UserRole;
import com.muti.domain.board.dto.request.CreatePostRequest;
import com.muti.domain.board.dto.request.UpdatePostRequest;
import com.muti.domain.board.dto.response.PostDetailDto;
import com.muti.domain.board.dto.response.PostDto;
import com.muti.domain.board.entity.Board;
import com.muti.domain.board.entity.Post;
import com.muti.domain.board.enums.BoardType;
import com.muti.domain.board.repository.CommentRepository;
import com.muti.domain.board.repository.PostLikeRepository;
import com.muti.domain.board.repository.PostRepository;
import com.muti.domain.auth.repository.UserRepository;
import com.muti.global.error.BusinessException;
import com.muti.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * PostService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PostService 테스트")
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private BoardService boardService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("게시글 작성 - 성공")
    void createPost_Success() {
        // given
        Long boardId = 1L;
        Long userId = 1L;

        CreatePostRequest request = CreatePostRequest.builder()
                .boardId(boardId)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        Board board = Board.builder()
                .id(boardId)
                .name("테스트게시판")
                .boardType(BoardType.FREE)
                .build();

        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .username("테스터")
                .role(UserRole.USER)
                .build();

        Post savedPost = Post.builder()
                .id(1L)
                .board(board)
                .user(user)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        given(boardService.getBoardEntity(boardId)).willReturn(board);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class))).willReturn(savedPost);

        // when
        PostDetailDto result = postService.createPost(request, userId);

        // then
        assertThat(result.getTitle()).isEqualTo("테스트 제목");
        assertThat(result.getContent()).isEqualTo("테스트 내용");

        verify(boardService).getBoardEntity(boardId);
        verify(userRepository).findById(userId);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 작성 - 존재하지 않는 게시판")
    void createPost_BoardNotFound() {
        // given
        CreatePostRequest request = CreatePostRequest.builder()
                .boardId(999L)
                .title("제목")
                .content("내용")
                .build();

        given(boardService.getBoardEntity(999L))
                .willThrow(new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> postService.createPost(request, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BOARD_NOT_FOUND);

        verify(boardService).getBoardEntity(999L);
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("게시글 상세 조회 - 성공 (조회수 증가)")
    void getPostDetail_Success() {
        // given
        Long postId = 1L;
        Long userId = 1L;

        Board board = Board.builder()
                .id(1L)
                .name("게시판")
                .boardType(BoardType.FREE)
                .build();

        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .username("테스터")
                .role(UserRole.USER)
                .build();

        Post post = Post.builder()
                .id(postId)
                .board(board)
                .user(user)
                .title("제목")
                .content("내용")
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(postLikeRepository.existsByPostIdAndUserId(postId, userId)).willReturn(false);

        // when
        PostDetailDto result = postService.getPostDetail(postId, userId);

        // then
        assertThat(result.getPostId()).isEqualTo(postId);
        assertThat(result.getTitle()).isEqualTo("제목");

        verify(postRepository).findById(postId);
        verify(postLikeRepository).existsByPostIdAndUserId(postId, userId);
    }

    @Test
    @DisplayName("게시글 수정 - 성공")
    void updatePost_Success() {
        // given
        Long postId = 1L;
        Long userId = 1L;

        UpdatePostRequest request = UpdatePostRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        Board board = Board.builder()
                .id(1L)
                .name("게시판")
                .boardType(BoardType.FREE)
                .build();

        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .username("테스터")
                .role(UserRole.USER)
                .build();

        Post post = Post.builder()
                .id(postId)
                .board(board)
                .user(user)
                .title("원래 제목")
                .content("원래 내용")
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(postLikeRepository.existsByPostIdAndUserId(postId, userId)).willReturn(false);

        // when
        PostDetailDto result = postService.updatePost(postId, request, userId);

        // then
        assertThat(result.getTitle()).isEqualTo("수정된 제목");
        assertThat(result.getContent()).isEqualTo("수정된 내용");

        verify(postRepository).findById(postId);
    }

    @Test
    @DisplayName("게시글 수정 - 권한 없음 (작성자 아님)")
    void updatePost_Forbidden() {
        // given
        Long postId = 1L;
        Long authorId = 1L;
        Long otherUserId = 2L;

        UpdatePostRequest request = UpdatePostRequest.builder()
                .title("수정 시도")
                .content("수정 시도")
                .build();

        Board board = Board.builder().id(1L).name("게시판").boardType(BoardType.FREE).build();
        User author = User.builder().id(authorId).email("author@example.com").username("작성자").role(UserRole.USER).build();
        Post post = Post.builder().id(postId).board(board).user(author).title("제목").content("내용").build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when & then
        assertThatThrownBy(() -> postService.updatePost(postId, request, otherUserId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_UPDATE_FORBIDDEN);

        verify(postRepository).findById(postId);
    }

    @Test
    @DisplayName("게시글 삭제 - 성공")
    void deletePost_Success() {
        // given
        Long postId = 1L;
        Long userId = 1L;

        Board board = Board.builder().id(1L).name("게시판").boardType(BoardType.FREE).build();
        User user = User.builder().id(userId).email("test@example.com").username("테스터").role(UserRole.USER).build();
        Post post = Post.builder().id(postId).board(board).user(user).title("제목").content("내용").build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        postService.deletePost(postId, userId);

        // then
        verify(postRepository).findById(postId);
        verify(commentRepository).deleteByPostId(postId);
        verify(postLikeRepository).deleteByPostId(postId);
        verify(postRepository).delete(post);
    }

    @Test
    @DisplayName("게시글 삭제 - 권한 없음")
    void deletePost_Forbidden() {
        // given
        Long postId = 1L;
        Long authorId = 1L;
        Long otherUserId = 2L;

        Board board = Board.builder().id(1L).name("게시판").boardType(BoardType.FREE).build();
        User author = User.builder().id(authorId).email("author@example.com").username("작성자").role(UserRole.USER).build();
        Post post = Post.builder().id(postId).board(board).user(author).title("제목").content("내용").build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when & then
        assertThatThrownBy(() -> postService.deletePost(postId, otherUserId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_DELETE_FORBIDDEN);

        verify(postRepository).findById(postId);
        verify(postRepository, never()).delete(any());
    }

    @Test
    @DisplayName("게시판별 게시글 목록 조회 - 성공")
    void getPostsByBoard_Success() {
        // given
        Long boardId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        Board board = Board.builder().id(boardId).name("게시판").boardType(BoardType.FREE).build();
        User user = User.builder().id(1L).email("test@example.com").username("테스터").role(UserRole.USER).build();

        Post post1 = Post.builder().id(1L).board(board).user(user).title("제목1").content("내용1").build();
        Post post2 = Post.builder().id(2L).board(board).user(user).title("제목2").content("내용2").build();

        Page<Post> postPage = new PageImpl<>(List.of(post1, post2), pageable, 2);

        given(boardService.getBoardEntity(boardId)).willReturn(board);
        given(postRepository.findByBoardId(boardId, pageable)).willReturn(postPage);

        // when
        Page<PostDto> result = postService.getPostsByBoard(boardId, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);

        verify(boardService).getBoardEntity(boardId);
        verify(postRepository).findByBoardId(boardId, pageable);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회 - 실패")
    void getPostDetail_NotFound() {
        // given
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.getPostDetail(999L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);

        verify(postRepository).findById(999L);
    }
}