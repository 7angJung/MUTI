package com.muti.muti.auth.oauth.dto;

import com.muti.muti.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String userId;
    private String email;
    private String provider;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String userId, String email, String provider) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.userId = userId;
        this.email = email;
        this.provider = provider;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao("id", attributes);
        }
        // 다른 소셜 로그인(google, naver 등) 추가 시 여기에 분기 처리
        return null;
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        if (kakaoAccount == null) {
            throw new IllegalStateException("카카오 계정 정보를 불러올 수 없습니다. 사용자 정보 제공에 동의했는지 확인해주세요.");
        }

        String email = (String) kakaoAccount.get("email");
        if (email == null) {
            throw new IllegalStateException("카카오 계정에서 이메일 정보를 불러올 수 없습니다. 이메일 정보 제공에 동의했는지 확인해주세요.");
        }

        return OAuthAttributes.builder()
                .userId(String.valueOf(attributes.get("id")))
                .email(email)
                .provider("kakao")
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity() {
        // OAuth2 신규 사용자의 경우, 비밀번호는 사용되지 않으므로 임의의 값을 저장
        String randomPassword = UUID.randomUUID().toString();
        // 소셜 가입 시 userId는 provider가 제공하는 고유 ID를 사용
        return new User(this.userId, randomPassword, this.email, this.provider);
    }
}
