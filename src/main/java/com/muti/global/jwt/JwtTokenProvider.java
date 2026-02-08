package com.muti.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * JWT Token Provider
 *
 * 역할: JWT 토큰의 생성, 검증, 파싱을 담당하는 핵심 클래스
 *
 * JWT 구조:
 * Header.Payload.Signature
 * eyJhbGc...  .  eyJzdWI...  .  SflKxw...
 *
 * 주요 기능:
 * 1. Access Token 생성 (15분 수명)
 * 2. Refresh Token 생성 (7일 수명)
 * 3. 토큰 검증 (서명 확인, 만료 체크)
 * 4. 토큰에서 사용자 정보 추출
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * SecretKey 생성
     *
     * - HMAC-SHA 알고리즘을 사용하여 비밀 키 생성
     * - application.yml의 secret 값을 바이트 배열로 변환
     * - 최소 256비트(32바이트) 필요
     *
     * @return 서명용 SecretKey
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access Token 생성
     *
     * 사용 시점: 로그인 성공 시, Refresh Token으로 갱신 시
     *
     * Payload에 포함되는 정보 (Claims):
     * - sub (Subject): 사용자 ID
     * - email: 사용자 이메일
     * - role: 사용자 권한 (USER, ADMIN)
     * - iat (Issued At): 발급 시간
     * - exp (Expiration): 만료 시간
     *
     * 왜 15분으로 짧게?
     * - 토큰 탈취 시 피해 최소화
     * - 만료 후 Refresh Token으로 자동 갱신
     *
     * @param userId 사용자 ID
     * @param email 사용자 이메일
     * @param role 사용자 권한 (예: "ROLE_USER")
     * @return JWT Access Token 문자열
     */
    public String createAccessToken(Long userId, String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .subject(String.valueOf(userId))                    // 사용자 ID를 subject로 설정
                .claim("email", email)                              // 이메일 추가
                .claim("role", role)                                // 권한 추가
                .issuedAt(now)                                      // 발급 시간
                .expiration(expiryDate)                             // 만료 시간
                .signWith(getSigningKey(), Jwts.SIG.HS512)         // HS512 알고리즘으로 서명
                .compact();                                         // 토큰 생성
    }

    /**
     * Refresh Token 생성
     *
     * 사용 시점: 로그인 성공 시
     *
     * Access Token과의 차이:
     * - 긴 수명 (7일)
     * - 최소한의 정보만 포함 (userId만)
     * - DB에 저장하여 관리
     * - HttpOnly Cookie에 저장하여 XSS 공격 방지
     *
     * 왜 7일로 길게?
     * - 사용자가 매번 로그인하지 않아도 됨
     * - Access Token이 만료될 때마다 자동 갱신
     * - 로그아웃 시 DB에서 삭제하여 무효화 가능
     *
     * @param userId 사용자 ID
     * @return JWT Refresh Token 문자열
     */
    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());

        return Jwts.builder()
                .subject(String.valueOf(userId))                    // 사용자 ID만 포함
                .issuedAt(now)                                      // 발급 시간
                .expiration(expiryDate)                             // 만료 시간 (7일)
                .signWith(getSigningKey(), Jwts.SIG.HS512)         // HS512 알고리즘으로 서명
                .compact();                                         // 토큰 생성
    }

    /**
     * 토큰에서 사용자 ID 추출
     *
     * 사용 시점: 모든 API 요청 시 (필터에서 호출)
     *
     * 과정:
     * 1. 토큰을 파싱하여 Payload 추출
     * 2. subject 필드에서 사용자 ID 추출
     * 3. String → Long 변환
     *
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 토큰에서 이메일 추출
     *
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    public String getEmailFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("email", String.class);
    }

    /**
     * 토큰에서 권한 추출
     *
     * @param token JWT 토큰
     * @return 사용자 권한 (예: "ROLE_USER")
     */
    public String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 토큰 파싱 (Payload 추출)
     *
     * 과정:
     * 1. 토큰을 분해하여 Header, Payload, Signature 추출
     * 2. Signature 검증 (서명이 올바른지 확인)
     * 3. Payload의 Claims 반환
     *
     * 예외 상황:
     * - 서명이 잘못된 경우: SignatureException
     * - 토큰이 만료된 경우: ExpiredJwtException
     * - 토큰 형식이 잘못된 경우: MalformedJwtException
     *
     * @param token JWT 토큰
     * @return Claims (Payload 내용)
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())                        // 서명 검증 키 설정
                .build()
                .parseSignedClaims(token)                           // 토큰 파싱 및 검증
                .getPayload();                                       // Claims 반환
    }

    /**
     * 토큰 유효성 검증
     *
     * 사용 시점: API 요청 시 필터에서 호출
     *
     * 검증 항목:
     * 1. 서명 검증: 토큰이 위조되지 않았는지 확인
     * 2. 만료 시간 검증: 토큰이 만료되지 않았는지 확인
     * 3. 형식 검증: JWT 형식이 올바른지 확인
     *
     * @param token JWT 토큰
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT 토큰입니다: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 JWT 토큰입니다: {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn("JWT 서명이 올바르지 않습니다: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 비어있습니다: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 토큰에서 Authentication 객체 생성
     *
     * 사용 시점: Spring Security의 SecurityContext에 인증 정보 저장 시
     *
     * Authentication이란?
     * - Spring Security에서 인증된 사용자 정보를 담는 객체
     * - SecurityContext에 저장되어 전역에서 접근 가능
     *
     * 과정:
     * 1. 토큰에서 userId, email, role 추출
     * 2. GrantedAuthority 리스트 생성 (권한 정보)
     * 3. UsernamePasswordAuthenticationToken 생성
     *    - principal: 사용자 식별 정보 (여기서는 userId)
     *    - credentials: 인증 정보 (여기서는 null, 이미 인증됨)
     *    - authorities: 권한 리스트
     *
     * @param token JWT 토큰
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        Long userId = getUserIdFromToken(token);
        String role = getRoleFromToken(token);

        // 권한 리스트 생성
        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(role)
        );

        // Authentication 객체 생성
        // principal: userId (주체 정보)
        // credentials: null (이미 인증됨)
        // authorities: 권한 리스트
        return new UsernamePasswordAuthenticationToken(userId, null, authorities);
    }

    /**
     * Refresh Token 만료 시간 조회
     *
     * 사용 시점: RefreshToken 엔티티 생성 시 expiryDate 설정
     *
     * @return Refresh Token 만료 시간 (밀리초)
     */
    public Long getRefreshTokenExpiration() {
        return jwtProperties.getRefreshTokenExpiration();
    }
}