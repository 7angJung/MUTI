package com.muti.domain.board.repository;

import com.muti.domain.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Comment Repository
 *
 * 역할: 댓글 데이터 접근 계층
 *
 * @author Claude Sonnet 4.5
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 게시글별 댓글 조회 (부모 댓글만, 생성일 순)
     *
     * @param postId 게시글 ID
     * @return 부모 댓글 목록
     */
    List<Comment> findByPostIdAndParentCommentIdIsNullOrderByCreatedAtAsc(Long postId);

    /**
     * 부모 댓글별 대댓글 조회 (생성일 순)
     *
     * @param parentCommentId 부모 댓글 ID
     * @return 대댓글 목록
     */
    List<Comment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId);

    /**
     * 게시글별 전체 댓글 조회 (부모 댓글 + 대댓글, 조인 fetch)
     *
     * 계층 구조 정렬:
     * - 부모 댓글을 먼저 정렬 (생성일 순)
     * - 각 부모 댓글의 대댓글들을 그 아래에 정렬
     *
     * COALESCE(c.parentComment.id, c.id):
     * - 부모 댓글인 경우: 자신의 id 사용
     * - 대댓글인 경우: 부모 댓글의 id 사용
     *
     * @param postId 게시글 ID
     * @return 계층 구조로 정렬된 전체 댓글 목록
     */
    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.user " +
            "WHERE c.post.id = :postId " +
            "ORDER BY COALESCE(c.parentComment.id, c.id), c.createdAt")
    List<Comment> findByPostIdWithUserOrderByHierarchy(@Param("postId") Long postId);

    /**
     * 게시글별 댓글 수 조회
     *
     * @param postId 게시글 ID
     * @return 댓글 수
     */
    Long countByPostId(Long postId);

    /**
     * 게시글별 모든 댓글 삭제
     *
     * @param postId 게시글 ID
     */
    void deleteByPostId(Long postId);

    /**
     * 게시글별 댓글 조회 (모든 댓글)
     *
     * @param postId 게시글 ID
     * @return 댓글 목록
     */
    List<Comment> findByPostId(Long postId);

    /**
     * 부모 댓글별 대댓글 조회
     *
     * @param parentCommentId 부모 댓글 ID
     * @return 대댓글 목록
     */
    List<Comment> findByParentCommentId(Long parentCommentId);

    /**
     * 게시글별 최상위 댓글 조회 (parentComment가 null)
     *
     * @param postId 게시글 ID
     * @return 최상위 댓글 목록
     */
    List<Comment> findByPostIdAndParentCommentIsNull(Long postId);

    /**
     * 사용자별 댓글 조회
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 댓글 목록
     */
    List<Comment> findByUserId(Long userId);
}