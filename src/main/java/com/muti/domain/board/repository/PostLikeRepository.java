package com.muti.domain.board.repository;

import com.muti.domain.board.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PostLike Repository
 *
 * 역할: 게시글 좋아요 데이터 접근 계층
 *
 * @author Claude Sonnet 4.5
 */
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    /**
     * 특정 사용자의 특정 게시글 좋아요 여부 확인
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 좋아요 존재 여부
     */
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    /**
     * 특정 사용자의 특정 게시글 좋아요 조회
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 좋아요 엔티티
     */
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    /**
     * 특정 사용자의 특정 게시글 좋아요 삭제
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     */
    void deleteByPostIdAndUserId(Long postId, Long userId);

    /**
     * 게시글별 좋아요 수 조회
     *
     * @param postId 게시글 ID
     * @return 좋아요 수
     */
    Long countByPostId(Long postId);

    /**
     * 게시글별 모든 좋아요 삭제
     *
     * @param postId 게시글 ID
     */
    void deleteByPostId(Long postId);

    /**
     * 게시글별 좋아요 목록 조회
     *
     * @param postId 게시글 ID
     * @return 좋아요 목록
     */
    List<PostLike> findByPostId(Long postId);

    /**
     * 사용자별 좋아요 목록 조회
     *
     * @param userId 사용자 ID
     * @return 좋아요 목록
     */
    List<PostLike> findByUserId(Long userId);
}