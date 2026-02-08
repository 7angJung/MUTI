package com.muti.domain.board.entity;

import com.muti.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Comment 엔티티
 *
 * 역할: 댓글/대댓글 정보를 저장하는 도메인 모델
 *
 * 주요 기능:
 * 1. 댓글 작성/삭제
 * 2. 대댓글 (parentComment 관계)
 *
 * @author Claude Sonnet 4.5
 */
@Entity
@Table(name = "comments")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

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