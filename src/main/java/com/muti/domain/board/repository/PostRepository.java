package com.muti.domain.board.repository;

import com.muti.domain.board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Post Repository
 *
 * 역할: 게시글 데이터 접근 계층
 *
 * @author Claude Sonnet 4.5
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 게시판별 게시글 조회 (페이징)
     *
     * @param boardId  게시판 ID
     * @param pageable 페이징 정보
     * @return 페이징된 게시글 목록
     */
    Page<Post> findByBoardId(Long boardId, Pageable pageable);

    /**
     * 조회수 증가 (벌크 연산)
     *
     * @param postId 게시글 ID
     */
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);

    /**
     * 사용자별 게시글 조회
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 게시글 목록
     */
    List<Post> findByUserId(Long userId);
}