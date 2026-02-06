package com.muti.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 기본 설정
 * 현재 단계: 모든 요청 허용 (인증 없음)
 * 향후 확장: OAuth2, JWT 추가 예정
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (REST API)
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 설정 (향후 필요 시 활성화)
                .cors(AbstractHttpConfigurer::disable)

                // 세션 사용하지 않음 (Stateless)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 모든 요청 허용 (현재 단계)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())

                // Form Login 비활성화
                .formLogin(AbstractHttpConfigurer::disable)

                // HTTP Basic 비활성화
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}