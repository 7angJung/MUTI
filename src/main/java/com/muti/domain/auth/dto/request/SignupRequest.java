package com.muti.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청 DTO
 *
 * 역할: 클라이언트로부터 회원가입 정보를 받는 객체
 *
 * Bean Validation이란?
 * - Java에서 제공하는 유효성 검증 표준 (JSR-380)
 * - 애노테이션으로 간단하게 검증 규칙 정의
 * - Spring이 자동으로 검증 수행
 *
 * 주요 애노테이션:
 * - @NotBlank: null, 빈 문자열, 공백만 있는 문자열 불가
 * - @Email: 이메일 형식 검증
 * - @Size: 문자열 길이 제한
 * - @Pattern: 정규표현식 패턴 매칭
 *
 * 사용 예시 (Controller에서):
 * public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
 *     // @Valid가 자동으로 검증 수행
 *     // 검증 실패 시 MethodArgumentNotValidException 발생
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {

    /**
     * 이메일 (로그인 ID)
     *
     * 검증 규칙:
     * - 필수 값 (@NotBlank)
     * - 이메일 형식 (@Email)
     * - 최대 100자 (@Size)
     *
     * 예시:
     * - 유효: "user@example.com"
     * - 무효: "", "invalid-email", "user@"
     */
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
    private String email;

    /**
     * 비밀번호
     *
     * 검증 규칙:
     * - 필수 값 (@NotBlank)
     * - 8~20자 (@Size)
     * - 영문, 숫자, 특수문자 포함 (@Pattern)
     *
     * 정규표현식 설명:
     * ^                       : 문자열 시작
     * (?=.*[A-Za-z])          : 영문 최소 1개 포함
     * (?=.*\d)                : 숫자 최소 1개 포함
     * (?=.*[@$!%*#?&])        : 특수문자 최소 1개 포함
     * [A-Za-z\d@$!%*#?&]{8,}  : 영문/숫자/특수문자로 8자 이상
     * $                       : 문자열 끝
     *
     * 예시:
     * - 유효: "password123!", "Test@1234"
     * - 무효: "12345678" (영문 없음), "password" (숫자/특수문자 없음)
     */
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다"
    )
    private String password;

    /**
     * 닉네임
     *
     * 검증 규칙:
     * - 필수 값 (@NotBlank)
     * - 2~50자 (@Size)
     *
     * 예시:
     * - 유효: "김철수", "John", "사용자123"
     * - 무효: "A" (너무 짧음), "" (빈 문자열)
     */
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 50, message = "닉네임은 2~50자여야 합니다")
    private String username;
}