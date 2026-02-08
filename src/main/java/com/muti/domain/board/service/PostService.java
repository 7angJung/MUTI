package com.muti.domain.board.service;

import com.muti.domain.auth.entity.User;
import com.muti.domain.auth.repository.UserRepository;
import com.muti.domain.board.dto.request.CreatePostRequest;
import com.muti.domain.board.dto.request.UpdatePostRequest;
import com.muti.domain.board.dto.response.PostDetailDto;
import com.muti.domain.board.dto.response.PostDto;
import com.muti.domain.board.entity.Board;
import com.muti.domain.board.entity.Post;
import com.muti.domain.board.repository.CommentRepository;
import com.muti.domain.board.repository.PostLikeRepository;
import com.muti.domain.board.repository.PostRepository;
import com.muti.global.error.BusinessException;
import com.muti.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 서비스
 *
 * 역할: 게시글 CRUD 비즈니스 로직 처리
 *
 * @author Claude Sonnet 4.5
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final BoardService boardService;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    /**
     * 게시판별 게시글 목록 조회 (페이징)
     *
     * @param boardId  게시판 ID
     * @param pageable 페이징 정보
     * @return 페이징된 게시글 목록
     */
    public Page<PostDto> getPostsByBoard(Long boardId, Pageable pageable) {
        log.info("게시판별 게시글 목록 조회: boardId={}, page={}, size={}",
                boardId, pageable.getPageNumber(), pageable.getPageSize());

        // 게시판 존재 확인
        boardService.getBoardEntity(boardId);

        Page<Post> posts = postRepository.findByBoardId(boardId, pageable);

        log.info("게시글 목록 조회 완료: boardId={}, totalElements={}", boardId, posts.getTotalElements());

        return posts.map(PostDto::from);
    }

    /**
     * 게시글 상세 조회 (조회수 증가)
     *
     * @param postId        게시글 ID
     * @param currentUserId 현재 사용자 ID (로그인하지 않은 경우 null)
     * @return 게시글 상세 정보
     */
    @Transactional
    public PostDetailDto getPostDetail(Long postId, Long currentUserId) {
        log.info("게시글 상세 조회: postId={}, userId={}", postId, currentUserId);

        Post post = getPostEntity(postId);

        // 조회수 증가
        post.incrementViewCount();

        // 좋아요 여부 확인
        boolean isLiked = currentUserId != null &&
                postLikeRepository.existsByPostIdAndUserId(postId, currentUserId);

        // 작성자 여부 확인
        boolean isAuthor = currentUserId != null && post.isAuthor(currentUserId);

        log.info("게시글 조회 완료: postId={}, viewCount={}", postId, post.getViewCount());

        return PostDetailDto.from(post, isLiked, isAuthor);
    }

    /**
     * 게시글 작성
     *
     * @param request 게시글 작성 요청
     * @param userId  작성자 ID
     * @return 작성된 게시글 정보
     */
    @Transactional
    public PostDetailDto createPost(CreatePostRequest request, Long userId) {
        log.info("게시글 작성: boardId={}, userId={}, title={}",
                request.getBoardId(), userId, request.getTitle());

        Board board = boardService.getBoardEntity(request.getBoardId());
        User user = getUserEntity(userId);

        Post post = Post.builder()
                .board(board)
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        Post savedPost = postRepository.save(post);

        log.info("게시글 작성 완료: postId={}", savedPost.getId());

        return PostDetailDto.from(savedPost, false, true);
    }

    /**
     * 게시글 수정
     *
     * @param postId  게시글 ID
     * @param request 게시글 수정 요청
     * @param userId  수정 요청 사용자 ID
     * @return 수정된 게시글 정보
     * @throws BusinessException 작성자가 아닌 경우
     */
    @Transactional
    public PostDetailDto updatePost(Long postId, UpdatePostRequest request, Long userId) {
        log.info("게시글 수정: postId={}, userId={}", postId, userId);

        Post post = getPostEntity(postId);

        // 작성자 확인
        if (!post.isAuthor(userId)) {
            log.warn("게시글 수정 권한 없음: postId={}, userId={}", postId, userId);
            throw new BusinessException(ErrorCode.POST_UPDATE_FORBIDDEN);
        }

        post.update(request.getTitle(), request.getContent());

        log.info("게시글 수정 완료: postId={}", postId);

        // 좋아요 여부 확인
        boolean isLiked = postLikeRepository.existsByPostIdAndUserId(postId, userId);

        return PostDetailDto.from(post, isLiked, true);
    }

    /**
     * 게시글 삭제
     *
     * @param postId 게시글 ID
     * @param userId 삭제 요청 사용자 ID
     * @throws BusinessException 작성자가 아닌 경우
     */
    @Transactional
    public void deletePost(Long postId, Long userId) {
        log.info("게시글 삭제: postId={}, userId={}", postId, userId);

        Post post = getPostEntity(postId);

        // 작성자 확인
        if (!post.isAuthor(userId)) {
            log.warn("게시글 삭제 권한 없음: postId={}, userId={}", postId, userId);
            throw new BusinessException(ErrorCode.POST_DELETE_FORBIDDEN);
        }

        // 연관된 댓글 먼저 삭제 (cascade)
        commentRepository.deleteByPostId(postId);
        log.debug("게시글의 댓글 삭제 완료: postId={}", postId);

        // 연관된 좋아요 먼저 삭제 (cascade)
        postLikeRepository.deleteByPostId(postId);
        log.debug("게시글의 좋아요 삭제 완료: postId={}", postId);

        // 게시글 삭제
        postRepository.delete(post);

        log.info("게시글 삭제 완료: postId={}", postId);
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