package com.muti.global.security.config;

import com.muti.global.security.filter.JwtAuthenticationFilter;
import com.muti.global.security.handler.JwtAccessDeniedHandler;
import com.muti.global.security.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 설정
 *
 * 역할: 애플리케이션의 보안 정책을 정의
 *
 * 주요 설정:
 * 1. 인증/인가 규칙 정의
 * 2. JWT 필터 등록
 * 3. 예외 처리 설정
 * 4. CORS 설정
 * 5. 비밀번호 암호화 설정
 *
 * @EnableWebSecurity: Spring Security 활성화
 * @EnableMethodSecurity: 메서드 레벨 보안 활성화
 *   - @PreAuthorize, @PostAuthorize, @Secured 등 사용 가능
 *   - 예: @PreAuthorize("hasRole('ADMIN')")
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * 비밀번호 암호화 Bean
     *
     * BCryptPasswordEncoder란?
     * - Spring Security가 권장하는 비밀번호 암호화 알고리즘
     * - 단방향 해시 함수 (복호화 불가능)
     * - 솔트(Salt) 자동 생성 (같은 비밀번호도 다른 해시 생성)
     * - 적응형 해싱 (시간이 지나도 안전하게 조정 가능)
     *
     * 사용 예시:
     * String rawPassword = "password123";
     * String encodedPassword = passwordEncoder.encode(rawPassword);
     * // 결과: $2a$10$N9qo8uLOickgx2ZMRZoMye...
     *
     * 비밀번호 검증:
     * boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
     *
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security Filter Chain 설정
     *
     * 역할: HTTP 보안 규칙 정의
     *
     * 필터 체인 순서:
     * 1. CORS Filter
     * 2. CSRF Filter (비활성화)
     * 3. JwtAuthenticationFilter (커스텀 필터)
     * 4. UsernamePasswordAuthenticationFilter
     * 5. ...기타 Spring Security 필터들
     * 6. Controller
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화
                // REST API는 Stateless하므로 CSRF 공격에 안전
                // JWT 토큰을 사용하므로 CSRF 토큰 불필요
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 세션 관리 설정
                // STATELESS: 세션을 생성하지 않고, JWT로 인증 관리
                // Spring Security가 세션을 생성하거나 사용하지 않음
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 인증/인가 규칙 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 URL (화이트리스트)
                        .requestMatchers(
                                "/api/v1/auth/**",              // 회원가입, 로그인, 토큰 갱신
                                "/api/v1/surveys/**",           // 설문 조회 (인증 불필요)
                                "/api/v1/boards/**",            // 게시판 관련 모든 경로
                                "/api/v1/posts/**",             // 게시글 관련 모든 경로
                                "/actuator/**",                 // Health Check
                                "/swagger-ui.html",             // Swagger UI HTML
                                "/swagger-ui/**",               // Swagger UI 리소스
                                "/v3/api-docs/**",              // Swagger API Docs
                                "/swagger-resources/**",        // Swagger 리소스
                                "/webjars/**",                  // Swagger Webjars
                                "/error"                        // 에러 페이지
                        ).permitAll()

                        // 관리자만 접근 가능한 URL
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 예외 처리 설정
                .exceptionHandling(exception -> exception
                        // 인증 실패 시 (401) 처리
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        // 인가 실패 시 (403) 처리
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                // JWT 필터 추가
                // UsernamePasswordAuthenticationFilter 이전에 실행
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * CORS 설정
     *
     * CORS (Cross-Origin Resource Sharing)란?
     * - 다른 도메인에서 리소스에 접근할 수 있도록 허용하는 정책
     * - 예: 프론트엔드(localhost:3000)에서 백엔드(localhost:8080) API 호출
     *
     * CORS가 필요한 이유:
     * - 웹 브라우저의 Same-Origin Policy 제약
     * - 프론트엔드와 백엔드가 다른 포트/도메인에서 실행되는 경우
     *
     * 설정 항목:
     * - allowedOriginPatterns: 허용할 도메인 패턴
     * - allowedMethods: 허용할 HTTP 메서드
     * - allowedHeaders: 허용할 헤더
     * - allowCredentials: 쿠키 전송 허용 여부
     * - maxAge: Preflight 요청 캐시 시간
     *
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 도메인 (개발 환경)
        // 프로덕션에서는 실제 도메인으로 변경 필요
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",           // React 개발 서버
                "http://localhost:5173",           // Vite 개발 서버
                "http://127.0.0.1:3000",
                "https://*.vercel.app",            // Vercel 배포
                "https://*.up.railway.app"         // Railway 배포
        ));

        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // 허용할 헤더
        configuration.setAllowedHeaders(List.of("*"));

        // 인증 정보 포함 허용 (쿠키, Authorization 헤더)
        configuration.setAllowCredentials(true);

        // Preflight 요청 캐시 시간 (1시간)
        configuration.setMaxAge(3600L);

        // 모든 경로에 CORS 설정 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}