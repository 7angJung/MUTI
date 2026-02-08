package com.muti.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Access Token 갱신 요청 DTO
 *
 * 역할: 만료된 Access Token을 새로 발급받기 위한 요청
 *
 * 사용 시점:
 * 1. 클라이언트가 API 요청 시 401 Unauthorized 응답 받음
 * 2. Access Token이 만료되었음을 인지
 * 3. 저장해둔 Refresh Token으로 갱신 요청
 * 4. 새로운 Access Token 발급 받음
 *
 * 요청 예시:
 * POST /api/v1/auth/refresh
 * {
 *   "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
 * }
 *
 * 응답 예시:
 * {
 *   "accessToken": "eyJhbGciOiJIUzUxMiJ9...",  // 새로 발급
 *   "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."   // 기존 유지 또는 새로 발급 (Rotation 전략)
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {

    /**
     * Refresh Token
     *
     * 클라이언트가 로그인 시 받은 Refresh Token
     * - 7일 유효
     * - DB에 저장된 토큰과 일치해야 함
     * - 만료되지 않아야 함
     */
    @NotBlank(message = "Refresh Token은 필수입니다")
    private String refreshToken;
}