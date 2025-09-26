package com.muti.muti.user.repository;

import com.muti.muti.user.domain.User;       // User 엔티티 임포트
import org.springframework.data.jpa.repository.JpaRepository;
// JpaRepository: JPA 기반 CRUD 기능을 제공하는 스프링 데이터 인터페이스
import java.util.Optional;                   // null 대신 안전하게 값을 감싸는 Optional 클래스

// User 엔티티를 위한 Repository 인터페이스
// JpaRepository<User, Long>을 상속 → User 엔티티를 Long 타입 PK로 다루는 CRUD 기능 자동 제공
public interface UserRepository extends JpaRepository<User, Long> {

    // username 컬럼으로 User 엔티티를 조회하는 메서드
    // 메서드 이름 규칙(findBy...)을 기반으로 스프링 데이터 JPA가 자동으로 구현체를 생성
    Optional<User> findByUserId(String userId);

    // email 컬럼으로 User 엔티티를 조회하는 메서드
    Optional<User> findByEmail(String email);
}