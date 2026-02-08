package com.muti.global.security.filter;

import com.muti.global.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터
 *
 * 역할: 모든 HTTP 요청에 대해 JWT 토큰을 검증하고 인증 정보를 설정
 *
 * OncePerRequestFilter란?
 * - Spring이 제공하는 필터 추상 클래스
 * - 요청당 한 번만 실행되도록 보장
 * - doFilterInternal() 메서드를 구현하여 필터 로직 작성
 *
 * 필터 체인 순서:
 * 1. Client 요청
 * 2. JwtAuthenticationFilter (여기!) ← JWT 검증
 * 3. Spring Security FilterChain
 * 4. Controller
 *
 * 작동 원리:
 * 1. HTTP 헤더에서 "Authorization: Bearer {token}" 추출
 * 2. JWT 유효성 검증 (서명, 만료)
 * 3. 토큰에서 사용자 정보 추출
 * 4. SecurityContext에 인증 정보 저장
 * 5. 다음 필터로 전달
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Authorization 헤더 이름
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Bearer 토큰 접두사
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 필터 로직 구현
     *
     * 과정:
     * 1. 요청 헤더에서 JWT 추출
     * 2. JWT 유효성 검증
     * 3. SecurityContext에 인증 정보 저장
     * 4. 다음 필터로 전달
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // 1. 요청에서 JWT 추출
            String token = resolveToken(request);

            // 2. 토큰이 있고, 유효한 경우
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

                // 3. 토큰에서 Authentication 객체 생성
                Authentication authentication = jwtTokenProvider.getAuthentication(token);

                // 4. SecurityContext에 인증 정보 저장
                // 이후 @AuthenticationPrincipal, SecurityContextHolder 등으로 접근 가능
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Security Context에 인증 정보 저장 완료, userId: {}",
                        authentication.getPrincipal());
            }
        } catch (Exception e) {
            // 토큰 검증 실패 시 로그만 남기고 계속 진행
            // SecurityContext가 비어있으므로 인증이 필요한 API는 401 반환
            log.error("JWT 인증 필터에서 예외 발생: {}", e.getMessage());
        }

        // 5. 다음 필터로 전달
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청에서 JWT 토큰 추출
     *
     * Authorization 헤더 형식:
     * Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
     *
     * 과정:
     * 1. Authorization 헤더 값 가져오기
     * 2. "Bearer " 접두사 확인
     * 3. "Bearer " 제거하고 토큰만 반환
     *
     * 예시:
     * Input:  "Bearer eyJhbGciOiJIUzUxMiJ9..."
     * Output: "eyJhbGciOiJIUzUxMiJ9..."
     *
     * @param request HTTP 요청
     * @return JWT 토큰 문자열 (없으면 null)
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        // Authorization 헤더가 있고, "Bearer "로 시작하는 경우
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            // "Bearer " 제거하고 토큰만 반환
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}