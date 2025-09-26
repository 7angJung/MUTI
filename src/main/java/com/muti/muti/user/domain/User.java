package com.muti.muti.user.domain;

import jakarta.persistence.*;       // JPA 어노테이션 (Entity, Id, Column 등)
import lombok.Getter;             // 롬복 - getter 자동 생성
import lombok.NoArgsConstructor;  // 롬복 - 기본 생성자 자동 생성
import lombok.Setter;             // 롬복 - setter 자동 생성

// JPA 엔티티 클래스임을 표시
@Entity
// DB 테이블 이름을 "users"로 지정 (기본은 클래스명 user → 여기선 명시적으로 지정)
@Table(name = "users")
@Getter   // 롬복: 모든 필드에 대한 getter 메서드 생성
@Setter   // 롬복: 모든 필드에 대한 setter 메서드 생성
@NoArgsConstructor // 롬복: 파라미터 없는 기본 생성자 자동 생성
public class User {

    // 기본 키(PK) 필드
    @Id
    // 기본 키 생성 전략: DB의 AUTO_INCREMENT 기능 사용 (MySQL에서 주로 사용)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // username 컬럼: not null, unique 제약 조건 추가
    @Column(nullable = false, unique = true)
    private String userId;

    // password 컬럼: not null, 중복 가능
    @Column(nullable = false)
    private String password;

    // email 컬럼: not null, unique 제약 조건 추가
    @Column(nullable = false, unique = true)
    private String email;

    private String mutiType; // e.g., "ISAP", "EFDU"

    @Column(nullable = false, length = 20)
    private String provider;

    // 사용자 정의 생성자 (회원가입 시 편하게 객체 생성 가능)
    public User(String userId, String password, String email) {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.provider = "local"; // 로컬 가입 시 기본값 설정
    }

    // 소셜 회원가입용 생성자
    public User(String userId, String password, String email, String provider) {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.provider = provider;
    }
}