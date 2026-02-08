package com.muti.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * RefreshToken 엔티티
 *
 * 역할: Refresh Token을 데이터베이스에 저장하여 관리
 *
 * Refresh Token을 DB에 저장하는 이유:
 * 1. 로그아웃 기능 구현: 해당 토큰을 DB에서 삭제하여 무효화
 * 2. 보안 강화: 탈취된 토큰을 서버에서 강제로 무효화 가능
 * 3. 토큰 재사용 방지: 동일한 토큰으로 여러 번 갱신 시도 감지
 * 4. 사용자 당 토큰 개수 제한: 한 사용자가 여러 기기에서 로그인 시 관리
 *
 * Access Token vs Refresh Token:
 * - Access Token: 짧은 수명(15분), DB에 저장 안 함, 메모리/LocalStorage에 보관
 * - Refresh Token: 긴 수명(7일), DB에 저장, HttpOnly Cookie에 보관
 */
@Entity
@Table(name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_user_id", columnList = "user_id"),  // user_id로 조회 최적화
                @Index(name = "idx_token", columnList = "token")        // token으로 조회 최적화
        })
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {

    /**
     * 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    /**
     * Refresh Token 문자열
     * - JWT 형식: eyJhbGc...
     * - unique = true: 중복 불가
     * - columnDefinition = "TEXT": 토큰이 길어질 수 있으므로 TEXT 타입 사용
     */
    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String token;

    /**
     * 사용자와의 연관 관계
     * - ManyToOne: 한 사용자는 여러 Refresh Token을 가질 수 있음
     *   (예: 핸드폰, 태블릿, PC에서 각각 로그인 시)
     * - FetchType.LAZY: 필요할 때만 User 정보를 가져옴 (성능 최적화)
     * - JoinColumn: 외래 키 컬럼명 지정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_refresh_token_user"))
    private User user;

    /**
     * 만료 시간
     * - Refresh Token의 유효 기간 (예: 현재 시간 + 7일)
     * - 이 시간이 지나면 토큰은 무효화됨
     */
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    /**
     * 토큰 생성 시간
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 토큰이 만료되었는지 확인
     *
     * @return true: 만료됨, false: 유효함
     *
     * 사용 예시:
     * if (refreshToken.isExpired()) {
     *     throw new ExpiredTokenException("토큰이 만료되었습니다");
     * }
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

    /**
     * 토큰 업데이트
     * - 기존 Refresh Token을 새로운 토큰으로 교체 (Rotation 전략)
     * - 보안 강화: 한 번 사용된 Refresh Token은 재사용 불가
     *
     * @param newToken 새로운 Refresh Token
     * @param expiryDate 새로운 만료 시간
     */
    public void updateToken(String newToken, LocalDateTime expiryDate) {
        this.token = newToken;
        this.expiryDate = expiryDate;
    }
}