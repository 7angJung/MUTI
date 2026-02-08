package com.muti.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 인증 실패 핸들러 (401 Unauthorized)
 *
 * 역할: 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출
 *
 * AuthenticationEntryPoint란?
 * - Spring Security에서 인증 실패 시 처리를 담당하는 인터페이스
 * - commence() 메서드를 구현하여 커스텀 응답 생성
 *
 * 401 Unauthorized가 발생하는 경우:
 * 1. JWT 토큰이 없는 경우
 * 2. JWT 토큰이 만료된 경우
 * 3. JWT 토큰이 유효하지 않은 경우
 * 4. JWT 서명이 올바르지 않은 경우
 *
 * 응답 예시:
 * {
 *   "error": "Unauthorized",
 *   "message": "인증이 필요합니다. 로그인 후 다시 시도해주세요.",
 *   "path": "/api/v1/users/me",
 *   "timestamp": "2024-02-07T10:30:00"
 * }
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * 인증 실패 시 호출되는 메서드
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param authException 인증 예외
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        // 로그 출력
        log.error("인증 실패: {} - {}", request.getRequestURI(), authException.getMessage());

        // 응답 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 에러 응답 본문 생성
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", "인증이 필요합니다. 로그인 후 다시 시도해주세요.");
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());

        // JSON 응답 작성
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}