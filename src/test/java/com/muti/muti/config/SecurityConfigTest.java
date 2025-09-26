package com.muti.muti.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    @DisplayName("Security Filter Chain에 OAuth2LoginAuthenticationFilter가 포함되어 있는지 확인")
    void shouldContainOAuth2LoginFilter() {
        boolean hasOAuth2LoginFilter = securityFilterChain.getFilters().stream()
                .anyMatch(filter -> filter instanceof OAuth2LoginAuthenticationFilter);

        assertThat(hasOAuth2LoginFilter).isTrue();
    }
}
