package com.muti.domain.board.controller;

import com.muti.domain.board.dto.request.CreatePostRequest;
import com.muti.domain.board.dto.request.UpdatePostRequest;
import com.muti.domain.board.dto.response.PostDetailDto;
import com.muti.domain.board.service.PostService;
import com.muti.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 게시글 API Controller
 *
 * 역할: 게시글 관련 REST API 엔드포인트 제공
 *
 * Base URL: /api/v1/posts
 *
 * 제공 API:
 * - POST /posts: 게시글 작성
 * - GET /posts/{postId}: 게시글 상세 조회
 * - PUT /posts/{postId}: 게시글 수정
 * - DELETE /posts/{postId}: 게시글 삭제
 *
 * @author Claude Sonnet 4.5
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 게시글 작성
     *
     * POST /api/v1/posts
     * Authorization: Bearer {accessToken} (필수)
     *
     * 요청 예시:
     * POST /api/v1/posts
     * Authorization: Bearer eyJhbGc...
     * {
     *   "boardId": 1,
     *   "title": "첫 게시글입니다",
     *   "content": "안녕하세요!"
     * }
     *
     * 응답 예시:
     * 201 Created
     * {
     *   "success": true,
     *   "data": {
     *     "postId": 1,
     *     "title": "첫 게시글입니다",
     *     "content": "안녕하세요!",
     *     ...
     *   }
     * }
     *
     * @param request 게시글 작성 요청
     * @return 201 Created + PostDetailDto
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PostDetailDto>> createPost(
            @Valid @RequestBody CreatePostRequest request) {
        Long userId = getCurrentUserId();

        log.info("POST /api/v1/posts - 게시글 작성: userId={}, boardId={}, title={}",
                userId, request.getBoardId(), request.getTitle());

        PostDetailDto post = postService.createPost(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(post));
    }

    /**
     * 게시글 상세 조회 (조회수 증가)
     *
     * GET /api/v1/posts/{postId}
     * Authorization: Bearer {accessToken} (선택)
     *
     * 응답 예시:
     * 200 OK
     * {
     *   "success": true,
     *   "data": {
     *     "postId": 1,
     *     "boardId": 1,
     *     "boardName": "자유게시판",
     *     "userId": 1,
     *     "username": "김철수",
     *     "title": "첫 게시글입니다",
     *     "content": "안녕하세요!",
     *     "viewCount": 1,
     *     "likeCount": 0,
     *     "commentCount": 0,
     *     "isLiked": false,
     *     "isAuthor": true,
     *     "createdAt": "2026-02-08T10:30:00",
     *     "updatedAt": "2026-02-08T10:30:00"
     *   }
     * }
     *
     * @param postId 게시글 ID
     * @return 200 OK + PostDetailDto
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailDto>> getPostDetail(@PathVariable Long postId) {
        Long userId = getCurrentUserIdOrNull();

        log.info("GET /api/v1/posts/{} - 게시글 상세 조회: userId={}", postId, userId);

        PostDetailDto post = postService.getPostDetail(postId, userId);

        return ResponseEntity.ok(ApiResponse.success(post));
    }

    /**
     * 게시글 수정
     *
     * PUT /api/v1/posts/{postId}
     * Authorization: Bearer {accessToken} (필수)
     *
     * 요청 예시:
     * PUT /api/v1/posts/1
     * Authorization: Bearer eyJhbGc...
     * {
     *   "title": "수정된 제목",
     *   "content": "수정된 내용"
     * }
     *
     * 응답 예시:
     * 200 OK
     * {
     *   "success": true,
     *   "data": {
     *     "postId": 1,
     *     "title": "수정된 제목",
     *     "content": "수정된 내용",
     *     ...
     *   }
     * }
     *
     * @param postId  게시글 ID
     * @param request 게시글 수정 요청
     * @return 200 OK + PostDetailDto
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailDto>> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest request) {
        Long userId = getCurrentUserId();

        log.info("PUT /api/v1/posts/{} - 게시글 수정: userId={}", postId, userId);

        PostDetailDto post = postService.updatePost(postId, request, userId);

        return ResponseEntity.ok(ApiResponse.success(post));
    }

    /**
     * 게시글 삭제
     *
     * DELETE /api/v1/posts/{postId}
     * Authorization: Bearer {accessToken} (필수)
     *
     * 응답 예시:
     * 204 No Content
     *
     * @param postId 게시글 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long postId) {
        Long userId = getCurrentUserId();

        log.info("DELETE /api/v1/posts/{} - 게시글 삭제: userId={}", postId, userId);

        postService.deletePost(postId, userId);

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
     * 인증되지 않은 경우 null 반환
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