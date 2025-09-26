package com.muti.muti.user.repository;

import com.muti.muti.user.domain.User;       // User 엔티티 임포트
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;                   // null 대신 안전하게 값을 감싸는 Optional 클래스

// JpaRepository: JPA 기반 CRUD 기능을 제공하는 스프링 데이터 인터페이스
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);

    // email 컬럼으로 User 엔티티를 조회하는 메서드
    Optional<User> findByEmail(String email);
}