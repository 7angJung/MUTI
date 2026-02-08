package com.muti.domain.board.entity;

import com.muti.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * PostLike 엔티티
 *
 * 역할: 게시글 좋아요 정보를 저장하는 도메인 모델
 *
 * 주요 기능:
 * 1. 좋아요 추가/삭제
 * 2. 중복 좋아요 방지 (post_id, user_id UNIQUE 제약)
 *
 * @author Claude Sonnet 4.5
 */
@Entity
@Table(name = "post_likes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_post_likes_post_user",
                columnNames = {"post_id", "user_id"}
        ))
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;
}