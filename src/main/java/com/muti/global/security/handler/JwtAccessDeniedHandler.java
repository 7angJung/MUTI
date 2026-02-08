package com.muti.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 인가 실패 핸들러 (403 Forbidden)
 *
 * 역할: 인증된 사용자가 권한이 없는 리소스에 접근할 때 호출
 *
 * AccessDeniedHandler란?
 * - Spring Security에서 인가 실패 시 처리를 담당하는 인터페이스
 * - handle() 메서드를 구현하여 커스텀 응답 생성
 *
 * 401 vs 403 차이:
 * - 401 Unauthorized: "누구세요?" - 인증 실패 (로그인 안 함)
 * - 403 Forbidden: "권한이 없어요!" - 인가 실패 (로그인은 했지만 권한 부족)
 *
 * 403 Forbidden이 발생하는 경우:
 * 1. 일반 사용자가 관리자 API 접근
 * 2. 다른 사용자의 리소스 접근 시도
 * 3. 역할 기반 접근 제어(RBAC) 위반
 *
 * 예시:
 * - USER 권한 사용자가 /admin/** 접근 시도 → 403
 * - 사용자 A가 사용자 B의 게시글 삭제 시도 → 403
 *
 * 응답 예시:
 * {
 *   "error": "Forbidden",
 *   "message": "해당 리소스에 대한 접근 권한이 없습니다.",
 *   "path": "/api/v1/admin/users",
 *   "timestamp": "2024-02-07T10:30:00"
 * }
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /**
     * 인가 실패 시 호출되는 메서드
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param accessDeniedException 접근 거부 예외
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        // 로그 출력
        log.error("접근 거부: {} - {}", request.getRequestURI(), accessDeniedException.getMessage());

        // 응답 설정
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 403
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 에러 응답 본문 생성
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Forbidden");
        errorResponse.put("message", "해당 리소스에 대한 접근 권한이 없습니다.");
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());

        // JSON 응답 작성
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}