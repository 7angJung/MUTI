package com.muti.domain.board.controller;

import com.muti.domain.board.dto.response.LikeResponse;
import com.muti.domain.board.service.PostLikeService;
import com.muti.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 게시글 좋아요 API Controller
 *
 * 역할: 게시글 좋아요 관련 REST API 엔드포인트 제공
 *
 * Base URL: /api/v1/posts/{postId}/like
 *
 * 제공 API:
 * - POST /posts/{postId}/like: 좋아요 토글 (추가/취소)
 *
 * @author Claude Sonnet 4.5
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/posts/{postId}/like")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    /**
     * 좋아요 토글 (좋아요 추가/취소)
     *
     * POST /api/v1/posts/{postId}/like
     * Authorization: Bearer {accessToken} (필수)
     *
     * 응답 예시 (좋아요 추가):
     * 200 OK
     * {
     *   "success": true,
     *   "data": {
     *     "isLiked": true,
     *     "likeCount": 1
     *   }
     * }
     *
     * 응답 예시 (좋아요 취소):
     * 200 OK
     * {
     *   "success": true,
     *   "data": {
     *     "isLiked": false,
     *     "likeCount": 0
     *   }
     * }
     *
     * @param postId 게시글 ID
     * @return 200 OK + LikeResponse
     */
    @PostMapping
    public ResponseEntity<ApiResponse<LikeResponse>> toggleLike(@PathVariable Long postId) {
        Long userId = getCurrentUserId();

        log.info("POST /api/v1/posts/{}/like - 좋아요 토글: userId={}", postId, userId);

        LikeResponse response = postLikeService.toggleLike(postId, userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * SecurityContext에서 현재 사용자 ID 추출
     *
     * @return 사용자 ID
     * @throws IllegalStateException 인증되지 않은 사용자인 경우
     */
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Long) {
            return (Long) principal;
        } else if (principal instanceof String) {
            String principalStr = (String) principal;
            if ("anonymousUser".equals(principalStr)) {
                throw new IllegalStateException("인증되지 않은 사용자입니다");
            }
            try {
                return Long.parseLong(principalStr);
            } catch (NumberFormatException e) {
                throw new IllegalStateException("인증되지 않은 사용자입니다");
            }
        }

        throw new IllegalStateException("인증되지 않은 사용자입니다");
    }
}