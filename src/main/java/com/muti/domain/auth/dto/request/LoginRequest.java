package com.muti.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청 DTO
 *
 * 역할: 클라이언트로부터 로그인 정보를 받는 객체
 *
 * 회원가입과의 차이:
 * - 비밀번호 검증 규칙이 단순함 (로그인은 이미 가입된 사용자)
 * - 닉네임 필드 없음
 *
 * 요청 예시:
 * POST /api/v1/auth/login
 * {
 *   "email": "user@example.com",
 *   "password": "password123!"
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    /**
     * 이메일 (로그인 ID)
     */
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    /**
     * 비밀번호
     *
     * 로그인 시에는 복잡한 검증 불필요
     * - 이미 가입된 사용자이므로 형식 검증 생략
     * - 빈 값만 체크
     */
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}