package com.muti.domain.board.service;

import com.muti.domain.auth.entity.User;
import com.muti.domain.auth.entity.UserRole;
import com.muti.domain.auth.repository.UserRepository;
import com.muti.domain.board.dto.response.LikeResponse;
import com.muti.domain.board.entity.Board;
import com.muti.domain.board.entity.Post;
import com.muti.domain.board.entity.PostLike;
import com.muti.domain.board.enums.BoardType;
import com.muti.domain.board.repository.PostLikeRepository;
import com.muti.domain.board.repository.PostRepository;
import com.muti.global.error.BusinessException;
import com.muti.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * PostLikeService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PostLikeService 테스트")
class PostLikeServiceTest {

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostLikeService postLikeService;

    @Test
    @DisplayName("좋아요 추가 - 성공")
    void toggleLike_AddLike_Success() {
        // given
        Long postId = 1L;
        Long userId = 1L;

        Board board = Board.builder().id(1L).name("게시판").boardType(BoardType.FREE).build();
        User user = User.builder().id(userId).email("test@example.com").username("테스터").role(UserRole.USER).build();
        Post post = Post.builder().id(postId).board(board).user(user).title("제목").content("내용").likeCount(0).build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(postLikeRepository.existsByPostIdAndUserId(postId, userId)).willReturn(false);

        PostLike savedLike = PostLike.builder()
                .id(1L)
                .post(post)
                .user(user)
                .build();
        given(postLikeRepository.save(any(PostLike.class))).willReturn(savedLike);

        // when
        LikeResponse result = postLikeService.toggleLike(postId, userId);

        // then
        assertThat(result.getIsLiked()).isTrue();
        assertThat(result.getLikeCount()).isEqualTo(1);

        verify(postRepository).findById(postId);
        verify(userRepository).findById(userId);
        verify(postLikeRepository).existsByPostIdAndUserId(postId, userId);
        verify(postLikeRepository).save(any(PostLike.class));
    }

    @Test
    @DisplayName("좋아요 취소 - 성공")
    void toggleLike_RemoveLike_Success() {
        // given
        Long postId = 1L;
        Long userId = 1L;

        Board board = Board.builder().id(1L).name("게시판").boardType(BoardType.FREE).build();
        User user = User.builder().id(userId).email("test@example.com").username("테스터").role(UserRole.USER).build();
        Post post = Post.builder().id(postId).board(board).user(user).title("제목").content("내용").likeCount(1).build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(postLikeRepository.existsByPostIdAndUserId(postId, userId)).willReturn(true);

        // when
        LikeResponse result = postLikeService.toggleLike(postId, userId);

        // then
        assertThat(result.getIsLiked()).isFalse();
        assertThat(result.getLikeCount()).isEqualTo(0);

        verify(postRepository).findById(postId);
        verify(userRepository).findById(userId);
        verify(postLikeRepository).existsByPostIdAndUserId(postId, userId);
        verify(postLikeRepository).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    @DisplayName("좋아요 토글 - 존재하지 않는 게시글")
    void toggleLike_PostNotFound() {
        // given
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postLikeService.toggleLike(999L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);

        verify(postRepository).findById(999L);
        verify(postLikeRepository, never()).existsByPostIdAndUserId(anyLong(), anyLong());
    }
}