package com.muti.domain.board.entity;

import com.muti.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Post 엔티티
 *
 * 역할: 게시글 정보를 저장하는 도메인 모델
 *
 * 주요 기능:
 * 1. 게시글 작성/수정/삭제
 * 2. 조회수, 좋아요 수, 댓글 수 관리
 * 3. 작성자와 게시판 연관 관계
 *
 * @author Claude Sonnet 4.5
 */
@Entity
@Table(name = "posts")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(nullable = false, name = "like_count")
    @Builder.Default
    private Integer likeCount = 0;

    @Column(nullable = false, name = "comment_count")
    @Builder.Default
    private Integer commentCount = 0;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 게시글 수정
     *
     * @param title   새 제목
     * @param content 새 내용
     */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * 조회수 증가
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * 좋아요 수 증가
     */
    public void incrementLikeCount() {
        this.likeCount++;
    }

    /**
     * 좋아요 수 감소
     */
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    /**
     * 댓글 수 증가
     */
    public void incrementCommentCount() {
        this.commentCount++;
    }

    /**
     * 댓글 수 감소
     */
    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    /**
     * 작성자 확인
     *
     * @param userId 확인할 사용자 ID
     * @return 작성자 본인이면 true, 아니면 false
     */
    public boolean isAuthor(Long userId) {
        return this.user.getId().equals(userId);
    }
}