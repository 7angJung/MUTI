package com.muti.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 테스트용 JPA Auditing 설정
 */
@TestConfiguration
@EnableJpaAuditing(dateTimeProviderRef = "testDateTimeProvider")
public class TestJpaConfig {

    @Bean
    public DateTimeProvider testDateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now());
    }
}