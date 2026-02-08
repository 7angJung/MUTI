package com.muti.domain.auth.controller;

import com.muti.domain.auth.dto.request.LoginRequest;
import com.muti.domain.auth.dto.request.RefreshTokenRequest;
import com.muti.domain.auth.dto.request.SignupRequest;
import com.muti.domain.auth.dto.response.AuthResponse;
import com.muti.domain.auth.dto.response.UserInfoResponse;
import com.muti.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 API Controller
 *
 * 역할: 인증 관련 REST API 엔드포인트 제공
 *
 * Base URL: /api/v1/auth
 *
 * 제공 API:
 * - POST /signup: 회원가입
 * - POST /login: 로그인
 * - POST /refresh: Access Token 갱신
 * - POST /logout: 로그아웃
 * - GET /me: 내 정보 조회
 *
 * @RestController란?
 * - @Controller + @ResponseBody
 * - 모든 메서드가 JSON 형태로 응답 반환
 *
 * @RequestMapping("/api/v1/auth")
 * - 이 컨트롤러의 모든 엔드포인트는 /api/v1/auth로 시작
 *
 * @Slf4j: 로깅을 위한 Logger 자동 생성
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입 API
     *
     * POST /api/v1/auth/signup
     *
     * @Valid란?
     * - DTO의 검증 애노테이션을 자동으로 체크
     * - 검증 실패 시 400 Bad Request 응답
     * - 예: @NotBlank, @Email, @Size 등
     *
     * 요청 예시:
     * POST /api/v1/auth/signup
     * {
     *   "email": "user@example.com",
     *   "password": "password123!",
     *   "username": "김철수"
     * }
     *
     * 응답 예시:
     * 201 Created
     * {
     *   "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
     *   "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
     *   "tokenType": "Bearer"
     * }
     *
     * @param request 회원가입 요청 DTO
     * @return 201 Created + AuthResponse
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        log.info("POST /api/v1/auth/signup - 회원가입 요청: email={}", request.getEmail());

        AuthResponse response = authService.signup(request);

        log.info("회원가입 완료: email={}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인 API
     *
     * POST /api/v1/auth/login
     *
     * 요청 예시:
     * POST /api/v1/auth/login
     * {
     *   "email": "user@example.com",
     *   "password": "password123!"
     * }
     *
     * 응답 예시:
     * 200 OK
     * {
     *   "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
     *   "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
     *   "tokenType": "Bearer"
     * }
     *
     * @param request 로그인 요청 DTO
     * @return 200 OK + AuthResponse
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/v1/auth/login - 로그인 요청: email={}", request.getEmail());

        AuthResponse response = authService.login(request);

        log.info("로그인 완료: email={}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * Access Token 갱신 API
     *
     * POST /api/v1/auth/refresh
     *
     * 사용 시점:
     * 1. 클라이언트가 API 요청 시 401 Unauthorized 응답
     * 2. Access Token 만료로 판단
     * 3. Refresh Token으로 새 Access Token 요청
     *
     * 요청 예시:
     * POST /api/v1/auth/refresh
     * {
     *   "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
     * }
     *
     * 응답 예시:
     * 200 OK
     * {
     *   "accessToken": "eyJhbGciOiJIUzUxMiJ9...",  // 새로 발급
     *   "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",  // 기존 유지
     *   "tokenType": "Bearer"
     * }
     *
     * @param request Refresh Token 요청 DTO
     * @return 200 OK + AuthResponse
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("POST /api/v1/auth/refresh - Access Token 갱신 요청");

        AuthResponse response = authService.refresh(request);

        log.info("Access Token 갱신 완료");
        return ResponseEntity.ok(response);
    }

    /**
     * 로그아웃 API
     *
     * POST /api/v1/auth/logout
     *
     * 과정:
     * 1. 서버: Refresh Token DB에서 삭제
     * 2. 클라이언트: Access Token 삭제 (메모리/LocalStorage)
     *
     * 요청 예시:
     * POST /api/v1/auth/logout
     * {
     *   "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
     * }
     *
     * 응답 예시:
     * 204 No Content
     *
     * @param request Refresh Token 요청 DTO
     * @return 204 No Content
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("POST /api/v1/auth/logout - 로그아웃 요청");

        authService.logout(request.getRefreshToken());

        log.info("로그아웃 완료");
        return ResponseEntity.noContent().build();
    }

    /**
     * 내 정보 조회 API
     *
     * GET /api/v1/auth/me
     *
     * @AuthenticationPrincipal이란?
     * - Spring Security의 SecurityContext에서 인증 정보 추출
     * - JwtAuthenticationFilter에서 설정한 userId를 가져옴
     * - 인증이 필요한 API (SecurityConfig에서 인증 필요로 설정)
     *
     * 작동 과정:
     * 1. 클라이언트가 Authorization 헤더에 JWT 포함하여 요청
     * 2. JwtAuthenticationFilter가 JWT 검증 및 userId 추출
     * 3. SecurityContext에 userId 저장
     * 4. @AuthenticationPrincipal이 userId를 메서드 파라미터로 주입
     * 5. Service에서 userId로 사용자 정보 조회
     *
     * 요청 예시:
     * GET /api/v1/auth/me
     * Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
     *
     * 응답 예시:
     * 200 OK
     * {
     *   "userId": 1,
     *   "email": "user@example.com",
     *   "username": "김철수",
     *   "role": "USER",
     *   "createdAt": "2024-02-07T10:30:00"
     * }
     *
     * @param userId 인증된 사용자 ID (SecurityContext에서 자동 주입)
     * @return 200 OK + UserInfoResponse
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo(@AuthenticationPrincipal Long userId) {
        log.info("GET /api/v1/auth/me - 내 정보 조회: userId={}", userId);

        UserInfoResponse response = authService.getUserInfo(userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 정보 조회 API (관리자용)
     *
     * GET /api/v1/auth/users/{userId}
     *
     * TODO: 관리자 권한 체크 추가
     * - @PreAuthorize("hasRole('ADMIN')")
     *
     * @param userId 조회할 사용자 ID
     * @return 200 OK + UserInfoResponse
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable Long userId) {
        log.info("GET /api/v1/auth/users/{} - 사용자 정보 조회", userId);

        UserInfoResponse response = authService.getUserInfo(userId);

        return ResponseEntity.ok(response);
    }
}