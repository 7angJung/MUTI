package com.muti.domain.board.entity;

import com.muti.domain.board.enums.BoardType;
import com.muti.domain.survey.enums.MutiType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Board 엔티티
 *
 * 역할: 게시판 정보를 저장하는 도메인 모델
 *
 * 주요 기능:
 * 1. 자유게시판: 모든 사용자가 접근 가능
 * 2. MUTI 타입 게시판: 특정 MUTI 타입 사용자만 접근 가능
 *
 * @author Claude Sonnet 4.5
 */
@Entity
@Table(name = "boards")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name = "board_type")
    private BoardType boardType;

    @Enumerated(EnumType.STRING)
    @Column(length = 4, name = "muti_type")
    private MutiType mutiType;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;
}