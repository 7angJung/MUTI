package com.muti.domain.music.repository;

import com.muti.domain.music.entity.Playlist;
import com.muti.domain.survey.enums.MutiType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 플레이리스트 Repository
 */
@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    /**
     * 사용자별 플레이리스트 조회
     */
    @Query("SELECT p FROM Playlist p " +
           "WHERE p.user.id = :userId " +
           "ORDER BY p.createdAt DESC")
    List<Playlist> findByUserId(@Param("userId") Long userId);

    /**
     * 공개 플레이리스트 조회 (페이징)
     */
    @Query("SELECT p FROM Playlist p " +
           "WHERE p.isPublic = true " +
           "ORDER BY p.createdAt DESC")
    Page<Playlist> findPublicPlaylists(Pageable pageable);

    /**
     * MUTI 타입별 플레이리스트 조회
     */
    List<Playlist> findByMutiType(MutiType mutiType);

    /**
     * MUTI 타입별 공개 플레이리스트 조회
     */
    @Query("SELECT p FROM Playlist p " +
           "WHERE p.mutiType = :mutiType " +
           "AND p.isPublic = true " +
           "ORDER BY p.createdAt DESC")
    List<Playlist> findPublicPlaylistsByMutiType(@Param("mutiType") MutiType mutiType);

    /**
     * 플레이리스트 이름으로 검색
     */
    @Query("SELECT p FROM Playlist p " +
           "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "AND p.isPublic = true")
    Page<Playlist> searchByName(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 사용자의 플레이리스트 개수
     */
    long countByUserId(Long userId);

    /**
     * 사용자의 플레이리스트 중 이름으로 찾기
     */
    Optional<Playlist> findByUserIdAndName(Long userId, String name);
}