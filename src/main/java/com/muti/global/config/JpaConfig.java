package com.muti.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 설정
 * Auditing 활성화로 생성/수정 시간 자동 관리
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}