package com.muti.domain.board.controller;

import com.muti.domain.board.dto.request.CreateCommentRequest;
import com.muti.domain.board.dto.response.CommentDto;
import com.muti.domain.board.service.CommentService;
import com.muti.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 댓글 API Controller
 *
 * 역할: 댓글 관련 REST API 엔드포인트 제공
 *
 * Base URL: /api/v1/posts/{postId}/comments
 *
 * 제공 API:
 * - GET /posts/{postId}/comments: 게시글별 댓글 목록 조회
 * - POST /posts/{postId}/comments: 댓글 작성
 * - DELETE /comments/{commentId}: 댓글 삭제
 *
 * @author Claude Sonnet 4.5
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 게시글별 댓글 목록 조회
     *
     * GET /api/v1/posts/{postId}/comments
     * Authorization: Bearer {accessToken} (선택)
     *
     * 응답 예시:
     * 200 OK
     * {
     *   "success": true,
     *   "data": [
     *     {
     *       "commentId": 1,
     *       "postId": 1,
     *       "userId": 1,
     *       "username": "김철수",
     *       "parentCommentId": null,
     *       "content": "첫 댓글입니다",
     *       "isAuthor": true,
     *       "createdAt": "2026-02-08T10:30:00",
     *       "replies": [
     *         {
     *           "commentId": 2,
     *           "parentCommentId": 1,
     *           "content": "대댓글입니다",
     *           ...
     *         }
     *       ]
     *     }
     *   ]
     * }
     *
     * @param postId 게시글 ID
     * @return 200 OK + List<CommentDto>
     */
    @GetMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentDto>>> getCommentsByPost(
            @PathVariable Long postId) {
        Long userId = getCurrentUserIdOrNull();

        log.info("GET /api/v1/posts/{}/comments - 댓글 목록 조회: userId={}", postId, userId);

        List<CommentDto> comments = commentService.getCommentsByPost(postId, userId);

        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    /**
     * 댓글 작성
     *
     * POST /api/v1/posts/{postId}/comments
     * Authorization: Bearer {accessToken} (필수)
     *
     * 요청 예시 (댓글):
     * POST /api/v1/posts/1/comments
     * Authorization: Bearer eyJhbGc...
     * {
     *   "content": "첫 댓글입니다",
     *   "parentCommentId": null
     * }
     *
     * 요청 예시 (대댓글):
     * {
     *   "content": "대댓글입니다",
     *   "parentCommentId": 1
     * }
     *
     * 응답 예시:
     * 201 Created
     * {
     *   "success": true,
     *   "data": {
     *     "commentId": 1,
     *     "content": "첫 댓글입니다",
     *     ...
     *   }
     * }
     *
     * @param postId  게시글 ID
     * @param request 댓글 작성 요청
     * @return 201 Created + CommentDto
     */
    @PostMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentDto>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request) {
        Long userId = getCurrentUserId();

        log.info("POST /api/v1/posts/{}/comments - 댓글 작성: userId={}, parentCommentId={}",
                postId, userId, request.getParentCommentId());

        CommentDto comment = commentService.createComment(postId, request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(comment));
    }

    /**
     * 댓글 삭제
     *
     * DELETE /api/v1/comments/{commentId}
     * Authorization: Bearer {accessToken} (필수)
     *
     * 응답 예시:
     * 204 No Content
     *
     * @param commentId 댓글 ID
     * @return 204 No Content
     */
    @DeleteMapping("/api/v1/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId) {
        Long userId = getCurrentUserId();

        log.info("DELETE /api/v1/comments/{} - 댓글 삭제: userId={}", commentId, userId);

        commentService.deleteComment(commentId, userId);

        return ResponseEntity.noContent().build();
    }

    /**
     * SecurityContext에서 현재 사용자 ID 추출 (필수)
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

    /**
     * SecurityContext에서 현재 사용자 ID 추출 (선택)
     *
     * @return 사용자 ID 또는 null
     */
    private Long getCurrentUserIdOrNull() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if ("anonymousUser".equals(principal)) {
            return null;
        }

        if (principal instanceof Long) {
            return (Long) principal;
        } else if (principal instanceof String) {
            try {
                return Long.parseLong((String) principal);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
}