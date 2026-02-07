package com.muti.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 설정
 * CORS(Cross-Origin Resource Sharing) 설정을 포함
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:3000",      // React 개발 서버
                        "http://localhost:5173",      // Vite 개발 서버
                        "http://127.0.0.1:5500",      // Live Server
                        "http://localhost:5500",      // Live Server
                        "http://127.0.0.1:8080",      // 백엔드 자체 호출
                        "https://*.vercel.app",       // Vercel 배포
                        "https://*.netlify.app"       // Netlify 배포
                )
                .allowedOriginPatterns("*")           // 개발 환경: 모든 origin 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}