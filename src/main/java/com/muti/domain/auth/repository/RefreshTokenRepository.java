package com.muti.domain.auth.repository;

import com.muti.domain.auth.entity.RefreshToken;
import com.muti.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * RefreshTokenRepository 인터페이스
 *
 * 역할: RefreshToken 엔티티에 대한 데이터베이스 접근 계층
 *
 * Refresh Token 관리 전략:
 * 1. 로그인 시: 새로운 Refresh Token 생성 및 저장
 * 2. Access Token 만료 시: Refresh Token으로 새로운 Access Token 발급
 * 3. Refresh Token도 만료 시: 재로그인 필요
 * 4. 로그아웃 시: Refresh Token 삭제
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Token 문자열로 RefreshToken 조회
     *
     * 사용 시점: Access Token 갱신 요청 시
     * - 클라이언트가 Refresh Token을 보내면 DB에서 조회
     * - 유효한 토큰인지, 만료되지 않았는지 확인
     *
     * 사용 예시:
     * Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByToken(token);
     * RefreshToken refreshToken = tokenOptional.orElseThrow(() ->
     *     new InvalidTokenException("유효하지 않은 Refresh Token입니다"));
     *
     * @param token Refresh Token 문자열
     * @return 토큰이 존재하면 Optional<RefreshToken>, 없으면 Optional.empty()
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 사용자로 RefreshToken 조회
     *
     * 사용 시점:
     * - 로그인 시 기존 토큰이 있는지 확인
     * - 사용자의 모든 토큰 조회 (여러 기기에서 로그인한 경우)
     *
     * 사용 예시:
     * Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
     * if (existingToken.isPresent()) {
     *     // 기존 토큰을 업데이트하거나 삭제
     * }
     *
     * @param user 사용자 엔티티
     * @return 토큰이 존재하면 Optional<RefreshToken>, 없으면 Optional.empty()
     */
    Optional<RefreshToken> findByUser(User user);

    /**
     * 사용자의 모든 RefreshToken 삭제
     *
     * 사용 시점:
     * - 모든 기기에서 로그아웃 (보안 조치)
     * - 비밀번호 변경 시 모든 기기의 로그인 무효화
     * - 계정 탈퇴 시
     *
     * @Modifying: 데이터를 변경하는 쿼리임을 명시 (INSERT, UPDATE, DELETE)
     * @Query: 직접 JPQL 쿼리 작성
     *
     * deleteBy + 조건: Spring Data JPA가 자동으로 삭제 쿼리 생성
     * - 실제 실행되는 SQL: DELETE FROM refresh_tokens WHERE user_id = ?
     *
     * 사용 예시:
     * refreshTokenRepository.deleteByUser(user);  // 해당 사용자의 모든 토큰 삭제
     *
     * @param user 사용자 엔티티
     */
    void deleteByUser(User user);

    /**
     * Token 문자열로 RefreshToken 삭제
     *
     * 사용 시점: 단일 기기에서 로그아웃
     * - 현재 사용 중인 기기의 토큰만 삭제
     * - 다른 기기는 계속 로그인 유지
     *
     * 사용 예시:
     * refreshTokenRepository.deleteByToken(token);  // 특정 토큰만 삭제
     *
     * @param token Refresh Token 문자열
     */
    void deleteByToken(String token);

    /**
     * 만료된 RefreshToken 일괄 삭제
     *
     * 사용 시점: 스케줄러를 통한 주기적 정리 (예: 매일 자정)
     * - DB에 만료된 토큰이 쌓이는 것을 방지
     * - 스토리지 비용 절감 및 성능 최적화
     *
     * @Modifying: 데이터 변경 쿼리
     * @Query: JPQL 직접 작성
     * - JPQL: Java Persistence Query Language (객체 지향 쿼리)
     * - SQL과 유사하지만 엔티티 객체를 대상으로 쿼리 작성
     *
     * :now: Named Parameter (메서드 파라미터 값이 바인딩됨)
     * - @Param("now")와 매칭됨
     *
     * 실제 실행되는 SQL:
     * DELETE FROM refresh_tokens WHERE expiry_date < ?
     *
     * 스케줄러 예시 (추후 구현):
     * @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정 실행
     * public void deleteExpiredTokens() {
     *     refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
     * }
     *
     * @param now 현재 시간
     * @return 삭제된 토큰 개수
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * Token 존재 여부 확인
     *
     * 사용 시점: 토큰 유효성 간단 체크
     *
     * @param token Refresh Token 문자열
     * @return 토큰이 존재하면 true, 없으면 false
     */
    boolean existsByToken(String token);
}