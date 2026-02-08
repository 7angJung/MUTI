package com.muti.domain.auth.repository;

import com.muti.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository 인터페이스
 *
 * 역할: User 엔티티에 대한 데이터베이스 접근 계층 (DAO)
 *
 * JpaRepository란?
 * - Spring Data JPA가 제공하는 인터페이스
 * - CRUD 메서드를 자동으로 제공 (save, findById, findAll, delete 등)
 * - 개발자가 직접 SQL을 작성하지 않아도 됨
 * - JpaRepository<엔티티 타입, ID 타입>
 *
 * 제네릭 타입 설명:
 * - User: 이 Repository가 관리하는 엔티티 클래스
 * - Long: User 엔티티의 기본 키(ID) 타입
 *
 * 기본 제공 메서드 (별도 구현 없이 사용 가능):
 * - save(user): 저장 또는 업데이트
 * - findById(id): ID로 조회
 * - findAll(): 전체 조회
 * - deleteById(id): ID로 삭제
 * - count(): 전체 개수
 * - existsById(id): 존재 여부 확인
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 조회
     *
     * 메서드 네이밍 규칙:
     * - findBy + 필드명: Spring Data JPA가 자동으로 쿼리 생성
     * - 실제 실행되는 SQL: SELECT * FROM users WHERE email = ?
     *
     * Optional이란?
     * - Java 8에서 도입된 null 처리를 위한 컨테이너 클래스
     * - 값이 있을 수도, 없을 수도 있음을 명시적으로 표현
     * - NullPointerException 방지
     *
     * 사용 예시:
     * Optional<User> userOptional = userRepository.findByEmail("test@example.com");
     * User user = userOptional.orElseThrow(() -> new UserNotFoundException());
     *
     * @param email 조회할 이메일
     * @return 사용자가 존재하면 Optional<User>, 없으면 Optional.empty()
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 중복 확인
     *
     * existsBy + 필드명: 해당 조건의 데이터가 존재하는지 boolean 반환
     * - 실제 실행되는 SQL: SELECT COUNT(*) > 0 FROM users WHERE email = ?
     * - count 쿼리를 사용하므로 findByEmail보다 성능이 좋음
     *
     * 사용 예시 (회원가입 시 이메일 중복 체크):
     * if (userRepository.existsByEmail(email)) {
     *     throw new DuplicateEmailException("이미 사용 중인 이메일입니다");
     * }
     *
     * @param email 확인할 이메일
     * @return 이메일이 존재하면 true, 없으면 false
     */
    boolean existsByEmail(String email);

    /**
     * 닉네임 중복 확인
     *
     * 사용 예시:
     * if (userRepository.existsByUsername(username)) {
     *     throw new DuplicateUsernameException("이미 사용 중인 닉네임입니다");
     * }
     *
     * @param username 확인할 닉네임
     * @return 닉네임이 존재하면 true, 없으면 false
     */
    boolean existsByUsername(String username);
}