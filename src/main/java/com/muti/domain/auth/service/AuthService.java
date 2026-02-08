package com.muti.domain.auth.service;

import com.muti.domain.auth.dto.request.LoginRequest;
import com.muti.domain.auth.dto.request.RefreshTokenRequest;
import com.muti.domain.auth.dto.request.SignupRequest;
import com.muti.domain.auth.dto.response.AuthResponse;
import com.muti.domain.auth.dto.response.UserInfoResponse;
import com.muti.domain.auth.entity.RefreshToken;
import com.muti.domain.auth.entity.User;
import com.muti.domain.auth.entity.UserRole;
import com.muti.domain.auth.repository.RefreshTokenRepository;
import com.muti.domain.auth.repository.UserRepository;
import com.muti.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 인증 서비스
 *
 * 역할: 회원가입, 로그인, 토큰 갱신, 로그아웃 등 인증 관련 비즈니스 로직 처리
 *
 * @Transactional이란?
 * - 메서드 실행 중 예외 발생 시 자동으로 롤백
 * - 여러 DB 작업을 하나의 트랜잭션으로 묶음
 * - readOnly = true: 조회 전용 (성능 최적화)
 *
 * 비유:
 * - Service = 은행 창구 직원
 * - Repository = 금고
 * - 직원이 고객 요청을 처리하고, 금고에서 데이터를 꺼내옴
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     *
     * 과정:
     * 1. 이메일 중복 확인
     * 2. 닉네임 중복 확인
     * 3. 비밀번호 암호화 (BCrypt)
     * 4. 사용자 저장
     * 5. JWT 토큰 발급
     * 6. Refresh Token DB에 저장
     *
     * @param request 회원가입 요청 DTO
     * @return AuthResponse (Access Token, Refresh Token)
     * @throws IllegalArgumentException 이메일 또는 닉네임 중복 시
     */
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        log.info("회원가입 시도: email={}", request.getEmail());

        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + request.getEmail());
        }

        // 2. 닉네임 중복 체크
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다: " + request.getUsername());
        }

        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 4. 사용자 생성 및 저장
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .username(request.getUsername())
                .role(UserRole.USER)  // 기본 권한: 일반 사용자
                .build();

        User savedUser = userRepository.save(user);
        log.info("회원가입 성공: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        // 5. JWT 토큰 발급
        return generateTokens(savedUser);
    }

    /**
     * 로그인
     *
     * 과정:
     * 1. 이메일로 사용자 조회
     * 2. 비밀번호 검증
     * 3. JWT 토큰 발급
     * 4. Refresh Token DB에 저장
     *
     * @param request 로그인 요청 DTO
     * @return AuthResponse (Access Token, Refresh Token)
     * @throws IllegalArgumentException 이메일 없음 또는 비밀번호 불일치
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("로그인 시도: email={}", request.getEmail());

        // 1. 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다: " + request.getEmail()));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        log.info("로그인 성공: userId={}, email={}", user.getId(), user.getEmail());

        // 3. JWT 토큰 발급
        return generateTokens(user);
    }

    /**
     * Access Token 갱신
     *
     * 과정:
     * 1. Refresh Token DB에서 조회
     * 2. 만료 여부 확인
     * 3. 새로운 Access Token 발급
     * 4. (선택) 새로운 Refresh Token 발급 (Rotation 전략)
     *
     * Refresh Token Rotation이란?
     * - 한 번 사용된 Refresh Token을 새로운 토큰으로 교체
     * - 보안 강화: 토큰 재사용 공격 방지
     * - 현재는 기존 Refresh Token 유지 (TODO: Rotation 추가)
     *
     * @param request Refresh Token 요청 DTO
     * @return AuthResponse (새로운 Access Token)
     * @throws IllegalArgumentException Refresh Token 없음 또는 만료
     */
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        log.info("Access Token 갱신 시도");

        // 1. Refresh Token 조회
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Refresh Token입니다"));

        // 2. 만료 확인
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("만료된 Refresh Token입니다. 다시 로그인해주세요.");
        }

        // 3. 사용자 정보 조회
        User user = refreshToken.getUser();

        // 4. 새로운 Access Token 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getKey()
        );

        log.info("Access Token 갱신 성공: userId={}", user.getId());

        // 기존 Refresh Token 유지
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .build();
    }

    /**
     * 로그아웃
     *
     * 과정:
     * 1. Refresh Token DB에서 삭제
     * 2. Access Token은 클라이언트에서 삭제 (서버에서 관리 안 함)
     *
     * Access Token은 왜 서버에서 무효화 안 하나?
     * - Stateless 특성 유지 (DB 조회 없이 검증 가능)
     * - 어차피 15분 후 자동 만료
     * - Refresh Token만 삭제하면 재로그인 필요
     *
     * @param refreshTokenValue Refresh Token 문자열
     */
    @Transactional
    public void logout(String refreshTokenValue) {
        log.info("로그아웃 시도");

        // Refresh Token 삭제
        refreshTokenRepository.findByToken(refreshTokenValue)
                .ifPresent(token -> {
                    refreshTokenRepository.delete(token);
                    log.info("로그아웃 성공: userId={}", token.getUser().getId());
                });
    }

    /**
     * 사용자 정보 조회
     *
     * @param userId 사용자 ID
     * @return UserInfoResponse
     * @throws IllegalArgumentException 사용자 없음
     */
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId));

        return UserInfoResponse.from(user);
    }

    /**
     * JWT 토큰 생성 및 저장
     *
     * 공통 로직:
     * 1. Access Token 생성
     * 2. Refresh Token 생성
     * 3. Refresh Token DB에 저장
     *
     * @param user 사용자 엔티티
     * @return AuthResponse (Access Token, Refresh Token)
     */
    private AuthResponse generateTokens(User user) {
        // 1. Access Token 생성 (15분)
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getKey()
        );

        // 2. Refresh Token 생성 (7일)
        String refreshTokenValue = jwtTokenProvider.createRefreshToken(user.getId());

        // 3. Refresh Token DB에 저장
        // 기존 토큰이 있으면 업데이트, 없으면 새로 생성
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .map(token -> {
                    // 기존 토큰 업데이트
                    LocalDateTime expiryDate = LocalDateTime.now()
                            .plusSeconds(jwtTokenProvider.getRefreshTokenExpiration() / 1000);
                    token.updateToken(refreshTokenValue, expiryDate);
                    return token;
                })
                .orElseGet(() -> {
                    // 새 토큰 생성
                    LocalDateTime expiryDate = LocalDateTime.now()
                            .plusSeconds(jwtTokenProvider.getRefreshTokenExpiration() / 1000);
                    return RefreshToken.builder()
                            .token(refreshTokenValue)
                            .user(user)
                            .expiryDate(expiryDate)
                            .build();
                });

        refreshTokenRepository.save(refreshToken);

        // 4. 응답 생성
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .build();
    }
}