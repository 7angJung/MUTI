package com.muti.domain.board.service;

import com.muti.domain.auth.entity.User;
import com.muti.domain.auth.repository.UserRepository;
import com.muti.domain.board.dto.request.CreateCommentRequest;
import com.muti.domain.board.dto.response.CommentDto;
import com.muti.domain.board.entity.Comment;
import com.muti.domain.board.entity.Post;
import com.muti.domain.board.repository.CommentRepository;
import com.muti.domain.board.repository.PostRepository;
import com.muti.global.error.BusinessException;
import com.muti.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 댓글 서비스
 *
 * 역할: 댓글 CRUD 비즈니스 로직 처리
 *
 * @author Claude Sonnet 4.5
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 게시글별 댓글 목록 조회 (계층 구조)
     *
     * @param postId        게시글 ID
     * @param currentUserId 현재 사용자 ID (로그인하지 않은 경우 null)
     * @return 계층 구조의 댓글 목록
     */
    public List<CommentDto> getCommentsByPost(Long postId, Long currentUserId) {
        log.info("게시글별 댓글 목록 조회: postId={}", postId);

        // 게시글 존재 확인
        getPostEntity(postId);

        // 모든 댓글 조회
        List<Comment> comments = commentRepository.findByPostIdWithUserOrderByHierarchy(postId);

        // 댓글 계층 구조 생성
        Map<Long, CommentDto> commentMap = new HashMap<>();
        List<CommentDto> rootComments = new ArrayList<>();

        for (Comment comment : comments) {
            boolean isAuthor = currentUserId != null && comment.isAuthor(currentUserId);
            CommentDto dto = CommentDto.from(comment, isAuthor);
            commentMap.put(dto.getCommentId(), dto);

            if (dto.getParentCommentId() == null) {
                // 부모 댓글
                rootComments.add(dto);
            } else {
                // 대댓글
                CommentDto parent = commentMap.get(dto.getParentCommentId());
                if (parent != null) {
                    parent.addReply(dto);
                }
            }
        }

        log.info("댓글 목록 조회 완료: postId={}, rootComments={}, totalComments={}",
                postId, rootComments.size(), comments.size());

        return rootComments;
    }

    /**
     * 댓글 작성
     *
     * @param postId  게시글 ID
     * @param request 댓글 작성 요청
     * @param userId  작성자 ID
     * @return 작성된 댓글 정보
     */
    @Transactional
    public CommentDto createComment(Long postId, CreateCommentRequest request, Long userId) {
        log.info("댓글 작성: postId={}, userId={}, parentCommentId={}",
                postId, userId, request.getParentCommentId());

        Post post = getPostEntity(postId);
        User user = getUserEntity(userId);

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = getCommentEntity(request.getParentCommentId());

            // 부모 댓글이 같은 게시글에 속하는지 확인
            if (!parentComment.getPost().getId().equals(postId)) {
                log.warn("부모 댓글이 해당 게시글에 속하지 않음: parentCommentId={}, postId={}",
                        request.getParentCommentId(), postId);
                throw new BusinessException(ErrorCode.COMMENT_PARENT_MISMATCH);
            }
        }

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .parentComment(parentComment)
                .content(request.getContent())
                .build();

        Comment savedComment = commentRepository.save(comment);

        // 게시글 댓글 수 증가
        post.incrementCommentCount();

        log.info("댓글 작성 완료: commentId={}", savedComment.getId());

        return CommentDto.from(savedComment, true);
    }

    /**
     * 댓글 삭제
     *
     * @param commentId 댓글 ID
     * @param userId    삭제 요청 사용자 ID
     * @throws BusinessException 작성자가 아닌 경우
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        log.info("댓글 삭제: commentId={}, userId={}", commentId, userId);

        Comment comment = getCommentEntity(commentId);

        // 작성자 확인
        if (!comment.isAuthor(userId)) {
            log.warn("댓글 삭제 권한 없음: commentId={}, userId={}", commentId, userId);
            throw new BusinessException(ErrorCode.COMMENT_DELETE_FORBIDDEN);
        }

        Post post = comment.getPost();

        commentRepository.delete(comment);

        // 게시글 댓글 수 감소
        post.decrementCommentCount();

        log.info("댓글 삭제 완료: commentId={}", commentId);
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
     * 댓글 ID로 조회 (내부 사용)
     *
     * @param commentId 댓글 ID
     * @return Comment 엔티티
     * @throws BusinessException 댓글을 찾을 수 없는 경우
     */
    private Comment getCommentEntity(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.warn("댓글을 찾을 수 없습니다: commentId={}", commentId);
                    return new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
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