package com.muti.muti.auth.oauth.handler;

import com.muti.muti.config.jwt.JwtUtil;
import com.muti.muti.user.domain.User;
import com.muti.muti.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        
        if (kakaoAccount == null) {
            throw new IllegalStateException("카카오 계정 정보를 불러올 수 없습니다.");
        }
        String email = (String) kakaoAccount.get("email");
        if (email == null) {
            throw new IllegalStateException("카카오 계정에서 이메일 정보를 불러올 수 없습니다.");
        }

        // OAuth2User에서 얻은 이메일로 우리 DB에서 사용자를 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("OAuth2 로그인 후 사용자를 찾을 수 없습니다."));

        // Spring Security의 UserDetails가 아닌, 우리 시스템의 User 엔티티를 기반으로 JWT 생성
        // UserDetails를 임시로 생성하여 토큰 발급
        org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(
                user.getUserId(), "", java.util.Collections.emptyList());

        String token = jwtUtil.generateToken(userDetails);

        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/index.html")
                .queryParam("token", token)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
