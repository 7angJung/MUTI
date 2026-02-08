package com.muti.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 인증 응답 DTO
 *
 * 역할: 로그인, 회원가입, 토큰 갱신 시 클라이언트에게 반환하는 객체
 *
 * 사용 시점:
 * 1. 회원가입 성공 시
 * 2. 로그인 성공 시
 * 3. Access Token 갱신 성공 시
 *
 * 응답 예시:
 * {
 *   "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWI...",
 *   "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWI...",
 *   "tokenType": "Bearer"
 * }
 *
 * 클라이언트 사용 방법:
 * 1. accessToken을 메모리 또는 LocalStorage에 저장
 * 2. refreshToken을 안전한 곳에 저장 (HttpOnly Cookie 권장)
 * 3. API 요청 시 Header에 포함:
 *    Authorization: Bearer {accessToken}
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    /**
     * Access Token
     *
     * - 15분 수명
     * - API 요청 시마다 사용
     * - Authorization 헤더에 포함
     */
    private String accessToken;

    /**
     * Refresh Token
     *
     * - 7일 수명
     * - Access Token 갱신 시에만 사용
     * - 안전하게 보관 필요
     */
    private String refreshToken;

    /**
     * 토큰 타입
     *
     * - 기본값: "Bearer"
     * - HTTP Authorization 헤더 형식: "Bearer {token}"
     */
    @Builder.Default
    private String tokenType = "Bearer";
}