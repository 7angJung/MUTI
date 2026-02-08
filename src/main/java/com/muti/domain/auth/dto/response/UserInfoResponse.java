package com.muti.domain.auth.dto.response;

import com.muti.domain.auth.entity.User;
import com.muti.domain.auth.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 정보 응답 DTO
 *
 * 역할: 인증된 사용자의 정보를 클라이언트에게 반환
 *
 * 사용 시점:
 * - GET /api/v1/users/me (내 정보 조회)
 * - GET /api/v1/users/{id} (특정 사용자 조회)
 *
 * Entity와의 차이:
 * - 비밀번호 제외 (보안)
 * - 필요한 정보만 노출
 * - 프론트엔드에 최적화된 형태
 *
 * 응답 예시:
 * {
 *   "userId": 1,
 *   "email": "user@example.com",
 *   "username": "김철수",
 *   "role": "USER",
 *   "createdAt": "2024-02-07T10:30:00"
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponse {

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 이메일
     */
    private String email;

    /**
     * 닉네임
     */
    private String username;

    /**
     * 권한
     */
    private String role;

    /**
     * 가입일
     */
    private LocalDateTime createdAt;

    /**
     * Entity → DTO 변환 메서드
     *
     * 왜 변환이 필요한가?
     * - Entity는 DB 구조에 맞춰진 형태
     * - DTO는 API 응답에 맞춰진 형태
     * - 비밀번호 등 민감한 정보 제외
     * - 필요한 필드만 선택적으로 노출
     *
     * 사용 예시:
     * User user = userRepository.findById(1L).orElseThrow();
     * UserInfoResponse response = UserInfoResponse.from(user);
     *
     * @param user User 엔티티
     * @return UserInfoResponse DTO
     */
    public static UserInfoResponse from(User user) {
        return UserInfoResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().name())  // Enum → String 변환
                .createdAt(user.getCreatedAt())
                .build();
    }
}