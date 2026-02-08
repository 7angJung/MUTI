package com.muti.global.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 설정 Properties
 *
 * 역할: application.yml의 jwt.* 설정 값을 읽어와서 관리
 *
 * @ConfigurationProperties란?
 * - application.yml의 설정 값을 Java 객체로 바인딩
 * - 타입 안정성: yml에서 숫자를 문자로 잘못 입력하면 컴파일 단계에서 오류 감지
 * - IDE 자동완성 지원
 *
 * prefix = "jwt": yml에서 jwt.* 로 시작하는 설정을 읽어옴
 * 예시:
 * jwt:
 *   secret: abc123
 *   access-token-expiration: 900000
 *
 * → JwtProperties의 secret = "abc123"
 * → JwtProperties의 accessTokenExpiration = 900000L
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 서명에 사용되는 비밀 키
     * - 최소 256비트(32자) 이상 권장
     * - 프로덕션 환경에서는 반드시 환경 변수로 설정 (JWT_SECRET)
     * - 절대 Git에 커밋하지 말 것!
     */
    private String secret;

    /**
     * Access Token 만료 시간 (밀리초)
     * - 기본값: 900000ms = 15분
     * - 짧은 수명으로 보안 강화
     */
    private Long accessTokenExpiration;

    /**
     * Refresh Token 만료 시간 (밀리초)
     * - 기본값: 604800000ms = 7일
     * - 긴 수명으로 사용자 편의성 향상
     */
    private Long refreshTokenExpiration;
}