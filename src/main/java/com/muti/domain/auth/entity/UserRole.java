package com.muti.domain.auth.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 권한 Enum
 *
 * 역할: 사용자의 권한 레벨을 정의
 *
 * Spring Security에서 사용하는 권한 체계:
 * - ROLE_ 접두사를 붙여서 사용 (예: ROLE_USER, ROLE_ADMIN)
 * - hasRole("USER")와 같은 방식으로 권한 체크
 *
 * 왜 Enum을 사용하는가?
 * 1. 타입 안정성: 오타로 인한 버그 방지 (예: "ADMN" 같은 오타 컴파일 단계에서 차단)
 * 2. 코드 가독성: USER, ADMIN처럼 명확한 의미 전달
 * 3. 유지보수성: 권한 추가 시 Enum에만 추가하면 됨
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {
    /**
     * 일반 사용자
     * - 설문 조사 참여 가능
     * - 자신의 결과 조회 가능
     * - 게시판 글 작성/수정/삭제 가능 (본인 글만)
     */
    USER("ROLE_USER", "일반 사용자"),

    /**
     * 관리자
     * - 모든 사용자 관리 가능
     * - 모든 게시글 관리 가능
     * - 설문 조사 생성/수정/삭제 가능
     * - 통계 데이터 조회 가능
     */
    ADMIN("ROLE_ADMIN", "관리자");

    /**
     * Spring Security에서 사용하는 권한 키
     * - "ROLE_" 접두사 포함
     */
    private final String key;

    /**
     * 사용자에게 표시할 권한 이름
     */
    private final String description;
}