package com.muti.domain.board.controller;

import com.muti.domain.board.dto.response.BoardDto;
import com.muti.domain.board.dto.response.PostDto;
import com.muti.domain.board.service.BoardService;
import com.muti.domain.board.service.PostService;
import com.muti.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 게시판 API Controller
 *
 * 역할: 게시판 관련 REST API 엔드포인트 제공
 *
 * Base URL: /api/v1/boards
 *
 * 제공 API:
 * - GET /boards: 게시판 목록 조회
 * - GET /boards/{boardId}/posts: 특정 게시판의 게시글 목록 조회 (페이징)
 *
 * @author Claude Sonnet 4.5
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final PostService postService;

    /**
     * 게시판 목록 조회
     *
     * GET /api/v1/boards
     *
     * 응답 예시:
     * 200 OK
     * {
     *   "success": true,
     *   "data": [
     *     {
     *       "boardId": 1,
     *       "name": "자유게시판",
     *       "description": "자유롭게 이야기를 나눠보세요!",
     *       "boardType": "FREE",
     *       "mutiType": null,
     *       "createdAt": "2026-02-08T10:30:00"
     *     },
     *     ...
     *   ]
     * }
     *
     * @return 200 OK + List<BoardDto>
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BoardDto>>> getAllBoards() {
        log.info("GET /api/v1/boards - 게시판 목록 조회");

        List<BoardDto> boards = boardService.getAllBoards();

        return ResponseEntity.ok(ApiResponse.success(boards));
    }

    /**
     * 특정 게시판의 게시글 목록 조회 (페이징)
     *
     * GET /api/v1/boards/{boardId}/posts?page=0&size=20&sort=createdAt,desc
     *
     * 요청 파라미터:
     * - page: 페이지 번호 (0부터 시작, 기본값: 0)
     * - size: 페이지 크기 (기본값: 20)
     * - sort: 정렬 기준 (기본값: createdAt,desc)
     *
     * 응답 예시:
     * 200 OK
     * {
     *   "success": true,
     *   "data": {
     *     "content": [...],
     *     "pageable": {...},
     *     "totalPages": 10,
     *     "totalElements": 200,
     *     "size": 20,
     *     "number": 0
     *   }
     * }
     *
     * @param boardId  게시판 ID
     * @param pageable 페이징 정보
     * @return 200 OK + Page<PostDto>
     */
    @GetMapping("/{boardId}/posts")
    public ResponseEntity<ApiResponse<Page<PostDto>>> getPostsByBoard(
            @PathVariable Long boardId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("GET /api/v1/boards/{}/posts - 게시판별 게시글 목록 조회: page={}, size={}",
                boardId, pageable.getPageNumber(), pageable.getPageSize());

        Page<PostDto> posts = postService.getPostsByBoard(boardId, pageable);

        return ResponseEntity.ok(ApiResponse.success(posts));
    }
}