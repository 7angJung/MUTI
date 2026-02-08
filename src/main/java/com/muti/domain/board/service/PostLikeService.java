package com.muti.domain.board.service;

import com.muti.domain.auth.entity.User;
import com.muti.domain.auth.repository.UserRepository;
import com.muti.domain.board.dto.response.LikeResponse;
import com.muti.domain.board.entity.Post;
import com.muti.domain.board.entity.PostLike;
import com.muti.domain.board.repository.PostLikeRepository;
import com.muti.domain.board.repository.PostRepository;
import com.muti.global.error.BusinessException;
import com.muti.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 좋아요 서비스
 *
 * 역할: 좋아요 토글 비즈니스 로직 처리
 *
 * @author Claude Sonnet 4.5
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 좋아요 토글 (좋아요 추가/취소)
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 좋아요 응답 (상태 + 현재 좋아요 수)
     */
    @Transactional
    public LikeResponse toggleLike(Long postId, Long userId) {
        log.info("좋아요 토글: postId={}, userId={}", postId, userId);

        Post post = getPostEntity(postId);
        User user = getUserEntity(userId);

        boolean isLiked;

        // 좋아요 존재 여부 확인
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            // 좋아요 취소
            postLikeRepository.deleteByPostIdAndUserId(postId, userId);
            post.decrementLikeCount();
            isLiked = false;
            log.info("좋아요 취소 완료: postId={}, userId={}", postId, userId);
        } else {
            // 좋아요 추가
            PostLike postLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            postLikeRepository.save(postLike);
            post.incrementLikeCount();
            isLiked = true;
            log.info("좋아요 추가 완료: postId={}, userId={}", postId, userId);
        }

        return LikeResponse.builder()
                .isLiked(isLiked)
                .likeCount(post.getLikeCount())
                .build();
    }

    /**
     * 게시글 ID로 조회 (내부 사용)
     *
     * @param postId 게시글 ID
     * @return Post 엔티티
     * @throws BusinessException 게시글을 찾을 수 없는 경우
     */
    private Post getPostEntity(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("게시글을 찾을 수 없습니다: postId={}", postId);
                    return new BusinessException(ErrorCode.POST_NOT_FOUND);
                });
    }

    /**
     * 사용자 ID로 조회 (내부 사용)
     *
     * @param userId 사용자 ID
     * @return User 엔티티
     * @throws BusinessException 사용자를 찾을 수 없는 경우
     */
    private User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("사용자를 찾을 수 없습니다: userId={}", userId);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });
    }
}