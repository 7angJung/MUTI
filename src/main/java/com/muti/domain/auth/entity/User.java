package com.muti.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * User 엔티티
 *
 * 역할: 시스템에 가입한 사용자의 정보를 저장하는 도메인 모델
 *
 * 주요 기능:
 * 1. 사용자 인증 정보 저장 (이메일, 비밀번호)
 * 2. 사용자 프로필 정보 관리 (닉네임)
 * 3. 권한 관리 (일반 사용자 / 관리자)
 * 4. 생성/수정 시간 자동 관리
 */
@Entity
@Table(name = "users")  // PostgreSQL에서 'user'는 예약어이므로 'users' 사용
@EntityListeners(AuditingEntityListener.class)  // JPA Auditing 기능 활성화 (생성/수정 시간 자동 관리)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA를 위한 기본 생성자, 외부에서 직접 생성 방지
@AllArgsConstructor
@Builder
public class User {

    /**
     * 기본 키 (Primary Key)
     * - 자동 증가 방식 (AUTO_INCREMENT in MySQL, SERIAL in PostgreSQL)
     * - Long 타입 사용 이유: Integer(약 21억)보다 큰 범위 지원, 서비스 확장성 고려
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    /**
     * 이메일 (로그인 ID로 사용)
     * - unique = true: 중복 불가, DB 레벨에서 제약 조건 생성
     * - nullable = false: 필수 값, NULL 불가
     * - 인덱스 자동 생성: unique 제약 조건으로 인해 성능 최적화
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * 비밀번호
     * - BCrypt로 암호화되어 저장됨 (예: $2a$10$N9qo8uLOickgx2ZMRZoMye...)
     * - 원문 비밀번호는 절대 저장하지 않음
     * - length = 255: BCrypt 해시는 약 60자이지만 여유있게 설정
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 사용자 닉네임
     * - 시스템 내에서 표시되는 이름
     * - nullable = false: 필수 값
     * - length = 50: 한글 기준 약 25자
     */
    @Column(nullable = false, length = 50)
    private String username;

    /**
     * 사용자 권한
     * - USER: 일반 사용자 (기본값)
     * - ADMIN: 관리자
     * - @Enumerated(EnumType.STRING): Enum을 문자열로 저장 (ORDINAL 대신)
     *   ORDINAL 사용 시 Enum 순서 변경 시 데이터 오류 발생 가능
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default  // Builder 패턴 사용 시 기본값 설정
    private UserRole role = UserRole.USER;

    /**
     * 계정 생성 시간
     * - @CreatedDate: JPA Auditing 기능으로 엔티티 생성 시 자동 설정
     * - updatable = false: 생성 이후 변경 불가
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 계정 수정 시간
     * - @LastModifiedDate: JPA Auditing 기능으로 엔티티 수정 시 자동 업데이트
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 비밀번호 변경 메서드
     *
     * @param encodedPassword BCrypt로 암호화된 비밀번호
     *
     * 사용 예시:
     * user.changePassword(passwordEncoder.encode("newPassword123"));
     */
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    /**
     * 닉네임 변경 메서드
     *
     * @param username 새로운 닉네임
     */
    public void changeUsername(String username) {
        this.username = username;
    }

    /**
     * 권한 변경 메서드 (관리자 전용)
     *
     * @param role 새로운 권한
     */
    public void changeRole(UserRole role) {
        this.role = role;
    }
}